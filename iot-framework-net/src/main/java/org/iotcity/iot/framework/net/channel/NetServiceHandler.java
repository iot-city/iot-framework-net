package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.PriorityRunnable;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.config.NetConfigService;
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
	private Object channelLock = new Object();

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
	public NetServiceHandler(NetManager manager, String serviceID) throws IllegalArgumentException {
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
	public boolean config(NetConfigService data, boolean reset) {
		if (data == null || !this.serviceID.equals(data.serviceID)) return false;
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
		// Get configure file.
		PropertiesConfigFile file = data.config;
		// Call configure.
		if (file != null) this.doConfig(file);
		// Return true.
		return true;
	}

	@Override
	public NetManager getNetManager() {
		return manager;
	}

	@Override
	public String getServiceID() {
		return serviceID;
	}

	@Override
	public long getMonitoringInterval() {
		return monitoringInterval;
	}

	@Override
	public NetServiceState getState() {
		return state;
	}

	@Override
	public boolean isStarted() {
		return state == NetServiceState.STARTED;
	}

	@Override
	public boolean isStopped() {
		return state == NetServiceState.STOPPED;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getStopTime() {
		return stopTime;
	}

	@Override
	public long getMessageTime() {
		return messageTime;
	}

	@Override
	public long getSentTime() {
		return sentTime;
	}

	// --------------------------- Channel methods ----------------------------

	@Override
	public NetChannel getChannel(String channelID) {
		return channels.get(channelID);
	}

	@Override
	public NetChannel[] filterChannels(NetChannelFilter filter) {
		List<NetChannel> list = new ArrayList<>();
		NetChannel[] channels = getChannels();
		for (NetChannel channel : channels) {
			if (filter.test(channel)) list.add(channel);
		}
		return list.toArray(new NetChannel[list.size()]);
	}

	@Override
	public NetChannel[] getChannels() {
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
	public int getChannelSize() {
		return channels.size();
	}

	// --------------------------- Inbound and outbound methods ----------------------------

	@Override
	public void addInbound(NetInbound<?, ?> inbound, int priority) {
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
	public void removeInbound(NetInbound<?, ?> inbound) {
		if (inbound == null) return;
		NetInboundContext context = inbounds.get(inbound.getIOClass());
		if (context != null) context.remove(inbound);
	}

	@Override
	public NetInboundObject[] getInbounds(Class<?> netIOClass) {
		NetInboundContext context = inbounds.get(netIOClass);
		return context == null ? null : context.getInbounds();
	}

	@Override
	public void addOutbound(NetOutbound<?, ?> outbound, int priority) {
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
	public void removeOutbound(NetOutbound<?, ?> outbound) {
		if (outbound == null) return;
		NetOutboundContext context = outbounds.get(outbound.getIOClass());
		if (context != null) context.remove(outbound);
	}

	@Override
	public NetOutboundObject[] getOutbounds(Class<?> netIOClass) {
		NetOutboundContext context = outbounds.get(netIOClass);
		return context == null ? null : context.getOutbounds();
	}

	// --------------------------- Start and stop methods ----------------------------

	@Override
	public boolean start() throws Exception {
		if (state == NetServiceState.STARTED) return true;
		synchronized (stateLock) {
			if (state == NetServiceState.STARTED) return true;

			// Publish starting event.
			NetEventFactory factory = this.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetServiceEvent event = factory.createServiceEvent(this, this, NetServiceState.STARTING);
			if (publisher.publish(event).isCancelled()) return false;

			// Do start logic.
			if (!doStart()) return false;
			state = NetServiceState.STARTED;
			startTime = System.currentTimeMillis();

			// Add a monitoring task.
			TaskHandler handler = manager.getTaskHandler();
			if (monitoringTaskID > 0) handler.remove(monitoringTaskID);
			monitoringTaskID = handler.addIntervalTask(monitoringTask, monitoringInterval, monitoringInterval);

			// Publish started event.
			publisher.publish(factory.createServiceEvent(this, this, NetServiceState.STARTED));
		}
		// Return started successfully.
		return true;
	}

	@Override
	public boolean stop() throws Exception {
		if (state == NetServiceState.STOPPED) return true;
		synchronized (stateLock) {
			if (state == NetServiceState.STOPPED) return true;

			// Publish stopping event.
			NetEventFactory factory = this.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetServiceEvent event = factory.createServiceEvent(this, this, NetServiceState.STOPPING);
			if (publisher.publish(event).isCancelled()) return false;

			// Destroy all channels.
			NetChannel[] allChannels = getChannels();
			for (NetChannel channel : allChannels) {
				// Close and destroy channel.
				channel.destroy();
			}
			// Do stop logic.
			if (!doStop()) return false;
			state = NetServiceState.STOPPED;
			stopTime = System.currentTimeMillis();
			channels.clear();
			channelArray = new NetChannel[0];

			// Remove the monitoring task.
			if (monitoringTaskID > 0) {
				manager.getTaskHandler().remove(monitoringTaskID);
				monitoringTaskID = 0;
			}

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
	 */
	void addChannel(NetChannel channel) {
		if (state != NetServiceState.STARTED) return;
		channels.put(channel.getChannelID(), channel);
		synchronized (channelLock) {
			if (!channelChanged) channelChanged = true;
		}
	}

	/**
	 * Remove a channel from this service.
	 * @param channelID The network channel unique identification.
	 */
	void removeChannel(String channelID) {
		channels.remove(channelID);
		synchronized (channelLock) {
			if (!channelChanged) channelChanged = true;
		}
	}

	/**
	 * Update the time to the system time in milliseconds that this service receives a message.
	 * @param channel The network channel object.
	 * @param time The current system time.
	 */
	void updateMessageTime(NetChannel channel, long time) {
		messageTime = time;
	}

	/**
	 * Update the time to the system time in milliseconds that this service sent a message.
	 * @param channel The network channel object.
	 * @param time The current system time.
	 */
	void updateSentTime(NetChannel channel, long time) {
		sentTime = time;
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Do service file configuration logic.
	 * @param file The configuration file (not null).
	 * @return Returns whether the service was successfully configured.
	 */
	protected abstract boolean doConfig(PropertiesConfigFile file);

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
