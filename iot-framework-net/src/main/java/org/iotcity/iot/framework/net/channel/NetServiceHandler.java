package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.PriorityRunnable;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.config.NetConfigInbound;
import org.iotcity.iot.framework.net.config.NetConfigOutbound;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.event.NetServiceEvent;
import org.iotcity.iot.framework.net.io.NetInbound;
import org.iotcity.iot.framework.net.io.NetOutbound;

/**
 * The network service handler.
 * @author ardon
 * @date 2021-06-19
 */
public abstract class NetServiceHandler implements NetService {

	// --------------------------- Public static fields ----------------------------

	/**
	 * The default value for service monitoring interval in milliseconds.
	 */
	public static final long CONST_MONITORING_INTERVAL = 10000;

	// --------------------------- Protected fields ----------------------------

	/**
	 * The logger object for this service.
	 */
	protected final Logger logger = FrameworkNet.getLogger();
	/**
	 * The locale text object for this service.
	 */
	protected final LocaleText locale = FrameworkNet.getLocale();
	/**
	 * The net manager object.
	 */
	protected final NetManager manager;
	/**
	 * The service unique identification.
	 */
	protected final String serviceID;
	/**
	 * The lock object for network state updating.
	 */
	protected final Object stateLock = new Object();
	/**
	 * Network state of this service.
	 */
	protected NetServiceState state = NetServiceState.CREATED;

	// --------------------------- Options fields ----------------------------

	/**
	 * The service monitoring interval in milliseconds for channel status check (10000 ms by default).
	 */
	protected long monitoringInterval = CONST_MONITORING_INTERVAL;

	// --------------------------- Private fields ----------------------------

	/**
	 * The network clients thread safe map (the key is channel ID, the value is channel object).
	 */
	private final Map<String, NetChannel> clients = new ConcurrentHashMap<>();
	/**
	 * The network channels thread safe map (the key is channel ID, the value is channel object).
	 */
	private final Map<String, NetChannel> channels = new ConcurrentHashMap<>();
	/**
	 * The channels array in this service.
	 */
	private NetChannel[] channelArray = new NetChannel[0];
	/**
	 * Indicates whether the network channels has changed.
	 */
	private boolean channelChanged = false;
	/**
	 * The lock for channels changed.
	 */
	private final Object channelLock = new Object();

	/**
	 * The inbounds map (the key is NetIO class, the value is inbound context object).
	 */
	private final Map<Class<?>, NetInboundContext> inbounds = new HashMap<>();
	/**
	 * The outbounds map (the key is NetIO class, the value is outbound context object).
	 */
	private final Map<Class<?>, NetOutboundContext> outbounds = new HashMap<>();

	/**
	 * The create time of this service in milliseconds.
	 */
	private final long createTime;
	/**
	 * The start time of this service in milliseconds.
	 */
	private long startTime;
	/**
	 * The stop time of this service in milliseconds.
	 */
	private long stopTime;
	/**
	 * The last time in milliseconds that this service receives a message.
	 */
	private long messageTime;
	/**
	 * The time in milliseconds that this service last sent a message.
	 */
	private long sentTime;

	// --------------------------- Service monitoring task ----------------------------

	/**
	 * The task ID for service status check.
	 */
	private long monitoringTaskID = 0;
	/**
	 * The monitoring task for service status check.
	 */
	private final Runnable monitoringTask = new PriorityRunnable(0) {

		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			NetChannel[] channels = getChannels();
			for (NetChannel channel : channels) {
				// Check channel running status.
				channel.checkRunningStatus(currentTime);
			}
		}

	};

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for network service handler.
	 * @param manager The net manager object (required, can not be null).
	 * @param serviceID The service unique identification (required, can not be or empty).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" or "serviceID" is null or empty.
	 */
	protected NetServiceHandler(NetManager manager, String serviceID) throws IllegalArgumentException {
		if (manager == null || StringHelper.isEmpty(serviceID)) {
			throw new IllegalArgumentException("Parameter manager and serviceID can not be null or empty!");
		}
		this.manager = manager;
		this.serviceID = serviceID;
		this.createTime = System.currentTimeMillis();
		// Publish created event.
		NetEventFactory factory = this.getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(factory.createServiceEvent(this, this, NetServiceState.CREATED));
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final boolean config(NetServiceOptions data, boolean reset) {
		// Returns true if there is no options data.
		if (data == null) return true;

		// Reset configuration data.
		if (reset) {
			this.clearInbounds();
			this.clearOutbounds();
		}

		// Fix the monitoring interval.
		long interval = data.monitoringInterval > 0 ? data.monitoringInterval : CONST_MONITORING_INTERVAL;
		// Check interval for monitoring task.
		if (monitoringInterval != interval) {
			monitoringInterval = interval;
			TaskHandler handler = manager.getTaskHandler();
			// Lock for task creation.
			synchronized (stateLock) {
				if (monitoringTaskID > 0) {
					handler.remove(monitoringTaskID);
					monitoringTaskID = handler.addIntervalTask(monitoringTask, interval, interval);
				}
			}
		}

		// Setup inbounds.
		NetConfigInbound[] ins = data.inbounds;
		if (ins != null && ins.length > 0) {
			for (NetConfigInbound config : ins) {
				if (config == null || config.instance == null) continue;
				try {
					this.addInbound(IoTFramework.getInstance(config.instance), config.priority);
				} catch (Exception e) {
					logger.error(e);
					return false;
				}
			}
		}

		// Setup outbounds.
		NetConfigOutbound[] outs = data.outbounds;
		if (outs != null && outs.length > 0) {
			for (NetConfigOutbound config : outs) {
				if (config == null || config.instance == null) continue;
				try {
					this.addOutbound(IoTFramework.getInstance(config.instance), config.priority);
				} catch (Exception e) {
					logger.error(e);
					return false;
				}
			}
		}

		// Return configuration result.
		return this.doConfig(data.config, reset);
	}

	@Override
	public final NetManager getNetManager() {
		return manager;
	}

	@Override
	public final String getServiceID() {
		return serviceID;
	}

	@Override
	public final long getMonitoringInterval() {
		return monitoringInterval;
	}

	@Override
	public final NetServiceState getState() {
		return state;
	}

	@Override
	public final boolean isStarted() {
		return state == NetServiceState.STARTED;
	}

	@Override
	public final boolean isStopped() {
		return state == NetServiceState.STOPPED;
	}

	@Override
	public final long getCreateTime() {
		return createTime;
	}

	@Override
	public final long getStartTime() {
		return startTime;
	}

	@Override
	public final long getStopTime() {
		return stopTime;
	}

	@Override
	public final long getMessageTime() {
		return messageTime;
	}

	@Override
	public final long getSentTime() {
		return sentTime;
	}

	// --------------------------- Client methods ----------------------------

	@Override
	public final void addClient(NetChannel channel) {
		if (channel == null || !channel.isClient()) return;

		// Add to the clients map.
		NetChannel removed = clients.put(channel.getChannelID(), channel);

		// Close removed client.
		if (removed != null && removed != channel) {
			try {
				removed.close();
			} catch (Exception e) {
				logger.error(e);
			}
		}

		// Open the client automatically.
		if (this.isStarted()) {
			try {
				channel.open();
			} catch (Exception e) {
				logger.error(locale.text("net.service.client.open.err", serviceID, channel.getChannelID(), e.getMessage()), e);
			}
		}
	}

	@Override
	public final NetChannel getClient(String channelID) {
		if (StringHelper.isEmpty(channelID)) return null;
		return clients.get(channelID);
	}

	@Override
	public final NetChannel removeClient(String channelID) {
		if (StringHelper.isEmpty(channelID)) return null;
		return clients.remove(channelID);
	}

	@Override
	public final NetChannel[] getClients() {
		return clients.values().toArray(new NetChannel[channels.size()]);
	}

	@Override
	public final int getClientSize() {
		return clients.size();
	}

	// --------------------------- Channel methods ----------------------------

	@Override
	public final NetChannel getChannel(String channelID) {
		if (StringHelper.isEmpty(channelID)) return null;
		return channels.get(channelID);
	}

	@Override
	public final NetChannel[] filterChannels(NetChannelFilter filter) {
		if (filter == null) return new NetChannel[0];
		List<NetChannel> list = new ArrayList<>();
		NetChannel[] channels = getChannels();
		for (NetChannel channel : channels) {
			if (filter.test(channel)) list.add(channel);
		}
		return list.toArray(new NetChannel[list.size()]);
	}

	@Override
	public final NetChannel[] getChannels() {
		if (channelChanged) {
			synchronized (channelLock) {
				if (channelChanged) {
					channelArray = channels.values().toArray(new NetChannel[channels.size()]);
					channelChanged = false;
				}
			}
		}
		return channelArray;
	}

	@Override
	public final int getChannelSize() {
		return channels.size();
	}

	// --------------------------- Inbound and outbound methods ----------------------------

	@Override
	public final void addInbound(NetInbound<?, ?> inbound, int priority) {
		if (inbound == null) return;
		NetInboundContext context = inbounds.get(inbound.getIOClass());
		if (context == null) {
			synchronized (inbounds) {
				context = inbounds.get(inbound.getIOClass());
				if (context == null) {
					context = new NetInboundContext();
					inbounds.put(inbound.getIOClass(), context);
				}
			}
		}
		context.add(inbound, priority);
	}

	@Override
	public final void removeInbound(NetInbound<?, ?> inbound) {
		if (inbound == null) return;
		NetInboundContext context = inbounds.get(inbound.getIOClass());
		if (context != null) context.remove(inbound);
	}

	@Override
	public final NetInboundObject[] getInbounds(Class<?> netIOClass) {
		NetInboundContext context = inbounds.get(netIOClass);
		return context == null ? null : context.getInbounds();
	}

	@Override
	public final void clearInbounds() {
		synchronized (inbounds) {
			inbounds.clear();
		}
	}

	@Override
	public final void addOutbound(NetOutbound<?, ?> outbound, int priority) {
		if (outbound == null) return;
		NetOutboundContext context = outbounds.get(outbound.getIOClass());
		if (context == null) {
			synchronized (outbounds) {
				context = outbounds.get(outbound.getIOClass());
				if (context == null) {
					context = new NetOutboundContext();
					outbounds.put(outbound.getIOClass(), context);
				}
			}
		}
		context.add(outbound, priority);
	}

	@Override
	public final void removeOutbound(NetOutbound<?, ?> outbound) {
		if (outbound == null) return;
		NetOutboundContext context = outbounds.get(outbound.getIOClass());
		if (context != null) context.remove(outbound);
	}

	@Override
	public final NetOutboundObject[] getOutbounds(Class<?> netIOClass) {
		NetOutboundContext context = outbounds.get(netIOClass);
		return context == null ? null : context.getOutbounds();
	}

	@Override
	public final void clearOutbounds() {
		synchronized (outbounds) {
			outbounds.clear();
		}
	}

	// --------------------------- Start and stop methods ----------------------------

	@Override
	public boolean start() throws Exception {
		if (state == NetServiceState.STARTED) return true;
		synchronized (stateLock) {
			if (state == NetServiceState.STARTED) return true;

			// Logs a message.
			logger.info(locale.text("net.service.starting", serviceID));

			// Publish starting event.
			NetEventFactory factory = this.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetServiceEvent event = factory.createServiceEvent(this, this, NetServiceState.STARTING);
			if (publisher.publish(event).isCancelled()) {
				logger.warn(locale.text("net.service.starting.cancelled", serviceID));
				return false;
			}

			// Reset to created state after stopping.
			if (state == NetServiceState.STOPPED) state = NetServiceState.CREATED;

			try {
				// Do start logic.
				if (!doStart()) {
					logger.warn(locale.text("net.service.start.failed", serviceID));
					return false;
				}
			} catch (Exception e) {
				logger.error(locale.text("net.service.start.err", serviceID, e.getMessage()), e);
				return false;
			}

			// Set started state.
			state = NetServiceState.STARTED;
			startTime = System.currentTimeMillis();

			// Add a monitoring task.
			TaskHandler handler = manager.getTaskHandler();
			if (monitoringTaskID > 0) handler.remove(monitoringTaskID);
			monitoringTaskID = handler.addIntervalTask(monitoringTask, monitoringInterval, monitoringInterval);

			// Logs a message.
			logger.info(locale.text("net.service.start.success", serviceID));
			// Publish started event.
			publisher.publish(factory.createServiceEvent(this, this, NetServiceState.STARTED));

		}

		// Check client size.
		if (clients.size() > 0) {
			// Get all clients to execute open method.
			NetChannel[] channels = getClients();
			// Logs a message.
			logger.info(locale.text("net.service.start.opening", channels.length, serviceID));
			// Traverse clients.
			for (NetChannel channel : channels) {
				try {
					channel.open();
				} catch (Exception e) {
					logger.error(locale.text("net.service.client.open.err", serviceID, channel.getChannelID(), e.getMessage()), e);
				}
			}
		}

		// Return started successfully.
		return true;
	}

	@Override
	public boolean stop() throws Exception {
		if (state == NetServiceState.STOPPED) return true;
		synchronized (stateLock) {
			if (state == NetServiceState.STOPPED) return true;

			// Logs a message.
			logger.info(locale.text("net.service.stopping", serviceID));

			// Publish stopping event.
			NetEventFactory factory = this.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetServiceEvent event = factory.createServiceEvent(this, this, NetServiceState.STOPPING);
			if (publisher.publish(event).isCancelled()) {
				logger.warn(locale.text("net.service.stopping.cancelled", serviceID));
				return false;
			}

			// Destroy all channels.
			NetChannel[] allChannels = getChannels();
			// Check channel size.
			if (allChannels.length > 0) {
				// Logs a message.
				logger.info(locale.text("net.service.stop.destroying", allChannels.length, serviceID));
				// Traverse channels.
				for (NetChannel channel : allChannels) {
					// Close and destroy channel.
					channel.destroy();
				}
			}

			try {
				// Do stop logic.
				if (!doStop()) {
					logger.warn(locale.text("net.service.stop.failed", serviceID));
					return false;
				}
			} catch (Exception e) {
				logger.error(locale.text("net.service.stop.err", serviceID, e.getMessage()), e);
				return false;
			}

			// Set stopped state.
			state = NetServiceState.STOPPED;
			stopTime = System.currentTimeMillis();
			channels.clear();
			channelArray = new NetChannel[0];

			// Remove the monitoring task.
			if (monitoringTaskID > 0) {
				manager.getTaskHandler().remove(monitoringTaskID);
				monitoringTaskID = 0;
			}

			// Logs a message.
			logger.info(locale.text("net.service.stop.success", serviceID));
			// Publish stopped event.
			publisher.publish(factory.createServiceEvent(this, this, NetServiceState.STOPPED));

		}
		// Return stopped successfully.
		return true;
	}

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Add a network channel to this service.
	 * @param channel The network channel object.
	 * @return Whether the addition to the service succeeded.
	 */
	final boolean addChannel(NetChannel channel) {
		if (state == NetServiceState.STOPPED) return false;
		synchronized (stateLock) {
			if (state == NetServiceState.STOPPED) return false;
			channels.put(channel.getChannelID(), channel);
		}
		synchronized (channelLock) {
			if (!channelChanged) channelChanged = true;
		}
		return true;
	}

	/**
	 * Remove a channel from this service.
	 * @param channel The network channel object.
	 */
	final void removeChannel(NetChannel channel) {
		NetChannel removed = channels.remove(channel.getChannelID());
		if (removed != null) {
			synchronized (channelLock) {
				if (!channelChanged) channelChanged = true;
			}
		}
	}

	/**
	 * Update the time to the system time in milliseconds that this service receives a message.
	 * @param channel The network channel object.
	 * @param time The current system time.
	 */
	final void updateMessageTime(NetChannel channel, long time) {
		messageTime = time;
	}

	/**
	 * Update the time to the system time in milliseconds that this service sent a message.
	 * @param channel The network channel object.
	 * @param time The current system time.
	 */
	final void updateSentTime(NetChannel channel, long time) {
		sentTime = time;
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Do service file configuration logic.
	 * @param file The configuration file (it is null when there is no configuration file).
	 * @param reset Whether reset the data of the current configurable object.
	 * @return Returns whether the service was successfully configured.
	 */
	protected abstract boolean doConfig(PropertiesConfigFile file, boolean reset);

	/**
	 * Do start service processing logic.
	 * @return Returns whether the service was successfully started.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doStart() throws Exception;

	/**
	 * Do stop service processing logic.
	 * @return Returns whether the service was successfully stopped.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doStop() throws Exception;

}
