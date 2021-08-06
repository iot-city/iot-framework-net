package org.iotcity.iot.framework.net;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.PriorityRunnable;
import org.iotcity.iot.framework.core.util.task.TaskGroupDataContext;
import org.iotcity.iot.framework.core.util.task.TaskGroupExecutor;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelSelector;
import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.config.NetConfig;
import org.iotcity.iot.framework.net.config.NetConfigPool;
import org.iotcity.iot.framework.net.config.NetConfigService;
import org.iotcity.iot.framework.net.io.NetDataRequest;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetMessageStatusCallback;
import org.iotcity.iot.framework.net.io.NetMessager;
import org.iotcity.iot.framework.net.io.NetRequester;
import org.iotcity.iot.framework.net.io.NetResponseCallback;
import org.iotcity.iot.framework.net.io.NetResponseResult;
import org.iotcity.iot.framework.net.io.NetResponseResultGroup;
import org.iotcity.iot.framework.net.session.NetSessionManager;

/**
 * The network manager is used to manage multiple network services and provide network data receiving and sending functions.
 * @author ardon
 * @date 2021-05-16
 */
public final class NetManager implements Configurable<NetConfig> {

	// ------------------------------------- Private fields -------------------------------------

	/**
	 * The session manager of this net manager.
	 */
	private final NetSessionManager sessions = new NetSessionManager();
	/**
	 * The network services map (the key is the service ID, the value is the service object).
	 */
	private final Map<String, NetService> services = new HashMap<>();
	/**
	 * The network messager object used to handle the message data from remote end.
	 */
	private final NetMessager messager = new NetMessager();
	/**
	 * The network requester object used to send the message data to remote end.
	 */
	private final NetRequester requester = new NetRequester();
	/**
	 * Task handler objects supporting thread pool to execute tasks and timer tasks (not null).
	 */
	private TaskHandler taskHandler;
	/**
	 * The maximum number of threads when using multithreading to send data.
	 */
	private int maximumThreadSize;

	// ------------------------------------- Constructor -------------------------------------

	/**
	 * Constructor for network manager instance.
	 */
	public NetManager() {
		this.setTaskHandler(TaskHandler.getDefaultHandler());
	}

	/**
	 * Constructor for network manager instance.
	 * @param taskHandler The task handler object supporting thread pool to execute tasks and timer tasks.
	 */
	public NetManager(TaskHandler taskHandler) {
		this.setTaskHandler(taskHandler);
	}

	// ------------------------------------- Public methods -------------------------------------

	/**
	 * Gets the session manager of this net manager (returns not null).
	 * @return The session manager to manage sessions.
	 */
	public NetSessionManager getSessionManager() {
		return sessions;
	}

	/**
	 * Gets the task handler in this network manager (returns not null).
	 * @return Task handler object.
	 */
	public TaskHandler getTaskHandler() {
		return taskHandler;
	}

	/**
	 * Set the task handler in this network manager.
	 * @param taskHandler The task handler in this network manager (required, can not be null).
	 */
	public void setTaskHandler(TaskHandler taskHandler) {
		if (taskHandler == null) return;
		this.taskHandler = taskHandler;
		this.maximumThreadSize = taskHandler.getThreadPoolExecutor().getMaximumPoolSize() / 3 - 1;
	}

	@Override
	public boolean config(NetConfig data, boolean reset) {
		if (data == null) return false;

		// Reset all services if necessary.
		if (reset) {
			// Stop and remove all services.
			this.clearServices(true);
		}

		// Do task handler configuration.
		NetConfigPool pool = data.pool;
		if (pool != null) {
			this.setTaskHandler(new TaskHandler("NetManager", pool.corePoolSize, pool.maximumPoolSize, pool.keepAliveTime, pool.capacity));
		}

		// Do session manager configuration.
		this.sessions.config(data.sessions, reset);

		// Do services configuration.
		NetConfigService[] svcsConfig = data.services;
		if (svcsConfig != null && svcsConfig.length > 0) {
			for (NetConfigService config : svcsConfig) {
				// Verify config data.
				if (!config.enabled) continue;

				// New service instance.
				Class<?> clazz = config.instance;
				NetService service = null;
				try {
					service = IoTFramework.getInstance(clazz, new Class<?>[] {
						NetManager.class,
						String.class
					}, new Object[] {
						this,
						config.serviceID
					});
				} catch (Exception e) {
					if (e instanceof NoSuchMethodException) {
						try {
							service = IoTFramework.getInstance(clazz);
						} catch (Exception e2) {
							FrameworkNet.getLogger().error(e2);
						}
					} else {
						FrameworkNet.getLogger().error(e);
					}
				}

				// Add to manager and config the service.
				if (!this.addService(service) || !service.config(config.options, reset)) {
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.manager.config.err", clazz == null ? null : clazz.getName(), config.serviceID));
					return false;
				}

				// Start service automatically.
				if (config.autoStart) {
					// Get task handler.
					TaskHandler handler = taskHandler;
					// Get task service.
					final NetService taskService = service;
					// Create service starting task.
					Runnable task = new PriorityRunnable(0) {

						@Override
						public void run() {
							try {
								taskService.start();
							} catch (Exception e) {
								FrameworkNet.getLogger().error(e);
							}
						}

					};
					// Start service.
					if (config.autoStartDelay > 0) {
						handler.addDelayTask(task, config.autoStartDelay);
					} else {
						handler.run(task);
					}
				}
			}
		}
		// Return true by default.
		return true;
	}

	// ------------------------------------- Service methods -------------------------------------

	/**
	 * Start all services.
	 */
	public void startAllServices() {
		NetService[] svcs = getServices();
		if (svcs.length == 0) return;
		for (NetService svc : svcs) {
			try {
				svc.start();
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * Stop all services.
	 */
	public void stopAllServices() {
		NetService[] svcs = getServices();
		if (svcs.length == 0) return;
		for (NetService svc : svcs) {
			try {
				svc.stop();
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * Add a network service to this manager. <br/>
	 * If the network service with the same service ID already exists in the manager or this manager is different from service manager object, the false value will be returned.
	 * @param service The network service object (required, can not be null).
	 * @return Returns whether the addition was successful.
	 */
	public boolean addService(NetService service) {
		if (service == null || service.getNetManager() != this) return false;
		String serviceID = service.getServiceID().toUpperCase();
		synchronized (services) {
			if (services.containsKey(serviceID)) return false;
			services.put(serviceID, service);
		}
		return true;
	}

	/**
	 * Remove a network service from this manager (returns null if the network service does not exist).
	 * @param serviceID The service unique identification (required, can not be null or empty).
	 * @return Returns the removed network service object.
	 */
	public NetService removeService(String serviceID) {
		if (StringHelper.isEmpty(serviceID)) return null;
		synchronized (services) {
			return services.remove(serviceID.toUpperCase());
		}
	}

	/**
	 * Remove all services from this manager (returns not null).
	 * @param stopOnRemoved Indicates whether to stop the service on removal.
	 * @return The services in this manager.
	 */
	public NetService[] clearServices(boolean stopOnRemoved) {
		synchronized (services) {
			NetService[] svcs = services.values().toArray(new NetService[services.size()]);
			if (stopOnRemoved) {
				for (NetService svc : svcs) {
					try {
						svc.stop();
					} catch (Exception e) {
						FrameworkNet.getLogger().error(e);
					}
				}
			}
			services.clear();
			return svcs;
		}
	}

	/**
	 * Gets a network service from this manager (returns null if the network service does not exist).
	 * @param serviceID The service unique identification (required, can not be null or empty).
	 * @return Returns the network service object for specified service ID.
	 */
	public NetService getService(String serviceID) {
		if (StringHelper.isEmpty(serviceID)) return null;
		return services.get(serviceID.toUpperCase());
	}

	/**
	 * Gets all network services in this manager (returns not null).
	 * @return The services in this manager.
	 */
	public NetService[] getServices() {
		synchronized (services) {
			return services.values().toArray(new NetService[services.size()]);
		}
	}

	/**
	 * Gets the service object size in this manager.
	 */
	public int getServiceSize() {
		return services.size();
	}

	// ------------------------------------- Message methods -------------------------------------

	/**
	 * Use the network I/O object to read and process inbound data.
	 * @param io The network input and output object (required, can not be null).
	 * @return Whether message processing succeeded or multithreading started successfully.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "io" is null.
	 */
	public boolean onMessage(NetIO<?, ?> io) throws IllegalArgumentException {
		return onMessage(io, null);
	}

	/**
	 * Use the network I/O object to read and process inbound data.
	 * @param io The network input and output object (required, can not be null).
	 * @param callback The network message status callback object (optional, set to null value when no callback is required).
	 * @return Whether message processing succeeded or multithreading started successfully.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "io" is null.
	 */
	public boolean onMessage(NetIO<?, ?> io, NetMessageStatusCallback callback) throws IllegalArgumentException {
		if (io == null) throw new IllegalArgumentException("Parameter io can not be null!");
		// Multithreading is used only for asynchronous response mode.
		if (io.isAsynchronous() && io.isMultithreading()) {

			// Get task handler.
			TaskHandler handler = taskHandler;
			// Run multithreading task.
			boolean submitted = handler.run(new PriorityRunnable(io.getChannel().getMultithreadingPriority()) {

				@Override
				public void run() {
					// Run message processing.
					NetMessageStatus status = messager.onMessage(io);
					// Callback message status.
					if (callback != null) callback.onCallback(status);
				}

			});
			// Determines whether the thread task is successfully submitted to the thread pool.
			if (submitted) return true;

			// Run message processing.
			NetMessageStatus status = messager.onMessage(io);
			// Callback message status.
			if (callback != null) callback.onCallback(status);
			// Return by message result status.
			return status == NetMessageStatus.OK || status == NetMessageStatus.ACCEPTED;

		} else {

			// Run message processing.
			NetMessageStatus status = messager.onMessage(io);
			// Callback message status.
			if (callback != null) callback.onCallback(status);
			// Return by message result status.
			return status == NetMessageStatus.OK || status == NetMessageStatus.ACCEPTED;

		}
	}

	// ------------------------------------- Asynchronous request methods -------------------------------------

	/**
	 * Use current thread to send request data to one remote end in asynchronous response mode.
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param channel The network channel object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return Whether the data request is executed successfully.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "channel", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> boolean asyncRequestOne(NetChannel channel, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) throws IllegalArgumentException {
		if (channel == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter channel, request and responseClass can not be null!");
		// Get to remote I/O object.
		NetIO<?, ?> io = channel.getToRemoteIO();
		// Only channels that send data to the remote end are supported.
		if (io == null) {
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.channel.io.err", channel.getClass().getName(), request.getClass().getName()));
		}
		// Send request to remote end.
		NetMessageStatus status = requester.asyncRequest(io, request, responseClass, callback, timeout);
		// Return by message result status.
		return status == NetMessageStatus.ACCEPTED || status == NetMessageStatus.OK;
	}

	/**
	 * Use current thread to send request data to all remote ends in asynchronous response mode.
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param selector The network channel selector object for channels selecting. (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for each channel response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The number of channels that successfully executed the data request.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "selector", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> int asyncRequestAll(NetChannelSelector selector, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) throws IllegalArgumentException {
		return asyncRequestAll(selector, request, responseClass, callback, timeout, 0, false);
	}

	/**
	 * Use multithreading to send request data to all remote ends in asynchronous response mode.
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param selector The network channel selector object for channels selecting. (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for each channel response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @param threads The maximum number of threads to send data (set to 0 if sending without multiple threads, the number of threads used will be limited to 1/3 of maximum pool size configured in the thread pool).
	 * @param expandCorePoolSize Whether to automatically expand the size of core threads in the thread pool according to the specified number of threads.
	 * @return The number of channels that successfully executed the data request.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "selector", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> int asyncRequestAll(NetChannelSelector selector, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout, int threads, boolean expandCorePoolSize) throws IllegalArgumentException {
		if (selector == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter selector, request and responseClass can not be null!");

		// Get channel objects.
		NetChannel[] channels = selector.getChannels();
		// Get data length.
		int length = channels == null ? 0 : channels.length;
		// Check length.
		if (length == 0) return 0;
		// Fix threads number.
		if (threads > maximumThreadSize) threads = maximumThreadSize;

		// Check threads.
		if (threads < 2 || length < 2) {

			// Define the number of successes.
			int successes = 0;
			// Traverses channels to send data one by one.
			for (NetChannel channel : channels) {
				// Send the request to remote end.
				if (asyncRequestOne(channel, request, responseClass, callback, timeout)) successes++;
			}
			// Return successes value.
			return successes;

		} else {

			// Get group timeout value.
			long groupTimeout = channels[0].fixCallbackTimeout(timeout) * length;
			// Create channel group data context.
			TaskGroupDataContext<NetChannel> context = new TaskGroupDataContext<NetChannel>(channels) {

				@Override
				public int getPriority(int index, NetChannel channel) {
					return channel == null ? 0 : channel.getMultithreadingPriority();
				}

				@Override
				public boolean run(int index, NetChannel channel) {
					// Send the request to remote end.
					return asyncRequestOne(channel, request, responseClass, callback, timeout);
				}

			};
			// Run channel request using task group executor.
			TaskGroupExecutor executor = new TaskGroupExecutor(taskHandler, context, threads, expandCorePoolSize, groupTimeout);
			// Do execution and return the result.
			return executor.execute();

		}
	}

	// ------------------------------------- Synchronous request methods -------------------------------------

	/**
	 * Use current thread to send request data to one remote end in synchronous response mode (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param channel The network channel object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The network response result data.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "channel", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> NetResponseResult<RES> syncRequestOne(NetChannel channel, REQ request, Class<RES> responseClass, long timeout) throws IllegalArgumentException {
		if (channel == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter channel, request and responseClass can not be null!");
		// Get to remote I/O object.
		NetIO<?, ?> io = channel.getToRemoteIO();
		// Only channels that send data to the remote end are supported.
		if (io == null) {
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.channel.io.err", channel.getClass().getName(), request.getClass().getName()));
		}
		// Send request to remote end.
		return requester.syncRequest(io, request, responseClass, timeout);
	}

	/**
	 * Use current thread to send request data to all remote ends in synchronous response mode (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param selector The network channel selector object for channels selecting. (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The network response result group data.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "selector", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> NetResponseResultGroup<RES> syncRequestAll(NetChannelSelector selector, REQ request, Class<RES> responseClass, long timeout) throws IllegalArgumentException {
		return syncRequestAll(selector, request, responseClass, timeout, 0, false);
	}

	/**
	 * Use multithreading to send request data to all remote ends in synchronous response mode (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param selector The network channel selector object for channels selecting. (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @param threads The maximum number of threads to send data (set to 0 if sending without multiple threads, the number of threads used will be limited to 1/3 of maximum pool size configured in the thread pool).
	 * @param expandCorePoolSize Whether to automatically expand the size of core threads in the thread pool according to the specified number of threads.
	 * @return The network response result group data.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "selector", "request" or "responseClass" is null.
	 */
	public <REQ extends NetDataRequest, RES extends NetDataResponse> NetResponseResultGroup<RES> syncRequestAll(NetChannelSelector selector, REQ request, Class<RES> responseClass, long timeout, int threads, boolean expandCorePoolSize) throws IllegalArgumentException {
		if (selector == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter selector, request and responseClass can not be null!");

		// Get channel objects.
		NetChannel[] channels = selector.getChannels();
		// Get data length.
		int length = channels == null ? 0 : channels.length;
		// Create the result array.
		@SuppressWarnings("unchecked")
		NetResponseResult<RES>[] results = (NetResponseResult<RES>[]) Array.newInstance(NetResponseResult.class, length);
		// Return empty array.
		if (length == 0) return new NetResponseResultGroup<>(length, results);
		// Fix threads number.
		if (threads > maximumThreadSize) threads = maximumThreadSize;

		// Check threads.
		if (threads < 2 || length < 2) {

			// Define the number of successes.
			int successes = 0;
			// Traverses channels to send data one by one.
			for (int i = 0; i < length; i++) {
				// Send the request to remote end.
				NetResponseResult<RES> result = syncRequestOne(channels[i], request, responseClass, timeout);
				// Set to result array.
				results[i] = result;
				// Get result status.
				NetMessageStatus status = result.getStatus();
				// Check result status.
				if (status == NetMessageStatus.OK || status == NetMessageStatus.ACCEPTED) successes++;
			}
			// Return result data.
			return new NetResponseResultGroup<>(successes, results);

		} else {

			// Get group timeout value.
			long groupTimeout = channels[0].fixCallbackTimeout(timeout) * length;
			// Create channel group data context.
			TaskGroupDataContext<NetChannel> context = new TaskGroupDataContext<NetChannel>(channels) {

				@Override
				public int getPriority(int index, NetChannel channel) {
					return channel == null ? 0 : channel.getMultithreadingPriority();
				}

				@Override
				public boolean run(int index, NetChannel channel) {
					// Send the request to remote end.
					NetResponseResult<RES> result = syncRequestOne(channel, request, responseClass, timeout);
					// Set to result array.
					results[index] = result;
					// Get result status.
					NetMessageStatus status = result.getStatus();
					// Check result status.
					return (status == NetMessageStatus.OK || status == NetMessageStatus.ACCEPTED);
				}

			};
			// Run channel request using task group executor.
			TaskGroupExecutor executor = new TaskGroupExecutor(taskHandler, context, threads, expandCorePoolSize, groupTimeout);
			// Do execution and get the successes.
			int successes = executor.execute();
			// Return result data.
			return new NetResponseResultGroup<>(successes, results);

		}

	}

}
