package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.NetManager;
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

	// --------------------------- Private fields ----------------------------

	/**
	 * The net manager object.
	 */
	protected final NetManager manager;
	/**
	 * The service unique identification.
	 */
	protected final String serviceID;
	/**
	 * The network service global configuration option data (not null).
	 */
	protected final NetServiceOptions options;
	/**
	 * The lock object for network state updating.
	 */
	protected final Object stateLock = new Object();
	/**
	 * Network state of this service.
	 */
	protected NetServiceState state = NetServiceState.CREATED;
	/**
	 * The network channels thread safe map (the key is channel ID, the value is channel object).
	 */
	protected final Map<String, NetChannel> channels = new ConcurrentHashMap<>();

	// --------------------------- Private fields ----------------------------

	/**
	 * Whether to use multithreading to process request and response data when allowed.
	 */
	private final boolean multithreading;
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

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for network service handler.
	 * @param manager The net manager object (required, can not be null).
	 * @param serviceID The service unique identification (required, can not be or empty).
	 * @param multithreading Whether to use multithreading to process request and response data when allowed.
	 * @param options The network service global configuration option data (optional).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" or "serviceID" is null or empty.
	 */
	public NetServiceHandler(NetManager manager, String serviceID, boolean multithreading, NetServiceOptions options) throws IllegalArgumentException {
		if (manager == null || StringHelper.isEmpty(serviceID)) {
			throw new IllegalArgumentException("Parameter service or channelID can not be null or empty!");
		}
		this.manager = manager;
		this.serviceID = serviceID;
		this.multithreading = multithreading;
		this.options = options == null ? new NetServiceOptions() : options;
		this.createTime = System.currentTimeMillis();
		// Add this service to manager.
		manager.addService(this);
		// Publish created event.
		NetEventFactory factory = this.getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(factory.createServiceEvent(this, this, NetServiceState.CREATED));
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public NetManager getNetManager() {
		return manager;
	}

	@Override
	public String getServiceID() {
		return serviceID;
	}

	@Override
	public boolean isMultithreading() {
		return multithreading;
	}

	@Override
	public int getMultithreadingPriority() {
		return options.multithreadingPriority;
	}

	@Override
	public long getDefaultCallbackTimeout() {
		return options.defaultCallbackTimeout;
	}

	@Override
	public NetServiceState getState() {
		return state;
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
		for (NetChannel channel : channels.values()) {
			if (filter.test(channel)) list.add(channel);
		}
		return list.toArray(new NetChannel[list.size()]);
	}

	@Override
	public NetChannel[] getChannels() {
		return channels.values().toArray(new NetChannel[channels.size()]);
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
			channels.clear();
			state = NetServiceState.STOPPED;
			stopTime = System.currentTimeMillis();

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
		channels.put(channel.getChannelID(), channel);
	}

	/**
	 * Remove a channel from this service.
	 * @param channelID The network channel unique identification.
	 */
	void removeChannel(String channelID) {
		channels.remove(channelID);
	}

	/**
	 * Update the time to the system time in milliseconds that this service receives a message.
	 * @param time The current system time.
	 */
	void updateMessageTime(long time) {
		messageTime = time;
	}

	/**
	 * Update the time to the system time in milliseconds that this service sent a message.
	 * @param time The current system time.
	 */
	void updateSentTime(long time) {
		sentTime = time;
	}

	// --------------------------- Abstract methods ----------------------------

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
