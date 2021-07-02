package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.event.NetChannelEvent;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.io.NetResponser;

/**
 * The network channel handler.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetChannelHandler implements NetChannel {

	// --------------------------- Protected fields ----------------------------

	/**
	 * Network channel service handler.
	 */
	protected final NetServiceHandler service;
	/**
	 * Network channel unique identification.
	 */
	protected final String channelID;
	/**
	 * The responser context to process asynchronous response callback message.
	 */
	protected final NetResponser responsers;
	/**
	 * The lock object for network state updating.
	 */
	protected final Object stateLock = new Object();
	/**
	 * Network state of this channel.
	 */
	protected NetChannelState state = NetChannelState.CREATED;

	// --------------------------- Private fields ----------------------------

	/**
	 * The create time of current channel in milliseconds.
	 */
	private final long createTime;
	/**
	 * The open time of current channel in milliseconds.
	 */
	private long openTime;
	/**
	 * The close time of current channel in milliseconds.
	 */
	private long closeTime;
	/**
	 * The last time in milliseconds that this channel receives a message.
	 */
	private long messageTime;
	/**
	 * The time in milliseconds that this channel last sent a message.
	 */
	private long sentTime;
	/**
	 * The stored data of this channel.
	 */
	private Object storedData;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for network channel handler.
	 * @param service Network channel service handler (required, not null).
	 * @param channelID Network channel unique identification (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "service" or "channelID" is null or empty.
	 */
	public NetChannelHandler(NetServiceHandler service, String channelID) throws IllegalArgumentException {
		if (service == null || StringHelper.isEmpty(channelID)) {
			throw new IllegalArgumentException("Parameter service and channelID can not be null or empty!");
		}
		this.service = service;
		this.channelID = channelID;
		this.createTime = System.currentTimeMillis();
		this.responsers = new NetResponser(service.getDefaultCallbackTimeout());
		// Publish created event.
		NetEventFactory factory = service.getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(factory.createChannelEvent(this, this, NetChannelState.CREATED));
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public NetService getService() {
		return service;
	}

	@Override
	public String getChannelID() {
		return channelID;
	}

	@Override
	public NetChannelState getState() {
		return state;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public long getOpenTime() {
		return openTime;
	}

	@Override
	public long getCloseTime() {
		return closeTime;
	}

	@Override
	public long getMessageTime() {
		return messageTime;
	}

	@Override
	public long getSentTime() {
		return sentTime;
	}

	@Override
	public void updateMessageTime() {
		messageTime = System.currentTimeMillis();
		service.updateMessageTime(messageTime);
	}

	@Override
	public void updateSentTime() {
		sentTime = System.currentTimeMillis();
		service.updateSentTime(sentTime);
	}

	@Override
	public NetResponser getResponser() {
		return responsers;
	}

	@Override
	public void setStoreData(Object data) {
		storedData = data;
	}

	@Override
	public <T> T getStoreData() {
		@SuppressWarnings("unchecked")
		T data = (T) storedData;
		return data;
	}

	@Override
	public boolean open() throws Exception {
		if (state == NetChannelState.OPENED) return true;
		synchronized (stateLock) {
			if (state == NetChannelState.OPENED) return true;

			// Publish opening event.
			NetEventFactory factory = service.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetChannelEvent event = factory.createChannelEvent(this, this, NetChannelState.OPENING);
			if (publisher.publish(event).isCancelled()) return false;

			// Do open logic.
			if (!doOpen()) return false;
			state = NetChannelState.OPENED;
			openTime = System.currentTimeMillis();
			// Add channel to service
			service.addChannel(this);

			// Publish opened event.
			publisher.publish(factory.createChannelEvent(this, this, NetChannelState.OPENED));
		}
		// Return opened successfully.
		return true;
	}

	@Override
	public boolean close() throws Exception {
		if (state == NetChannelState.CLOSED) return true;
		synchronized (stateLock) {
			if (state == NetChannelState.CLOSED) return true;

			// Publish closing event.
			NetEventFactory factory = service.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetChannelEvent event = factory.createChannelEvent(this, this, NetChannelState.CLOSING);
			if (publisher.publish(event).isCancelled()) return false;

			// Callback response on closing.
			responsers.callbackOnClosing();
			// Do close logic.
			if (!doClose()) return false;
			state = NetChannelState.CLOSED;
			closeTime = System.currentTimeMillis();
			// Remove channel from service.
			service.removeChannel(channelID);

			// Publish closed event.
			publisher.publish(factory.createChannelEvent(this, this, NetChannelState.CLOSED));
		}
		// Return closed successfully.
		return true;
	}

	@Override
	public void destroy() {
		if (state == NetChannelState.CLOSED) return;
		synchronized (stateLock) {
			if (state == NetChannelState.CLOSED) return;
			// Callback response on closing.
			responsers.callbackOnClosing();
			// Do close logic.
			try {
				doClose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			state = NetChannelState.CLOSED;
			closeTime = System.currentTimeMillis();
			// Remove channel from service.
			service.removeChannel(channelID);
		}
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Do open channel processing logic.
	 * @return Returns whether the channel was successfully opened.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doOpen() throws Exception;

	/**
	 * Do close channel processing logic.
	 * @return Returns whether the channel was successfully closed.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doClose() throws Exception;

}
