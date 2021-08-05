package org.iotcity.iot.framework.net.channel;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.config.NetConfigInbound;
import org.iotcity.iot.framework.net.config.NetConfigOutbound;
import org.iotcity.iot.framework.net.event.NetChannelEvent;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.io.NetInbound;
import org.iotcity.iot.framework.net.io.NetOutbound;
import org.iotcity.iot.framework.net.io.NetResponser;

/**
 * The network channel handler.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetChannelHandler implements NetChannel {

	// --------------------------- Public static fields ----------------------------

	/**
	 * The default value for multithreading options.
	 */
	public static final boolean CONST_MULTITHREADING = false;
	/**
	 * The default value for thread execution priority.
	 */
	public static final int CONST_MULTITHREADING_PRIORITY = 0;
	/**
	 * The default value for callback timeout in milliseconds.
	 */
	public static final long CONST_CALLBACK_TIMEOUT = 120000;
	/**
	 * The default value for receiving idle timeout in milliseconds.
	 */
	public static final long CONST_RECEIVING_IDLE_TIMEOUT = 0;
	/**
	 * The default value for sending idle timeout in milliseconds.
	 */
	public static final long CONST_SENDING_IDLE_TIMEOUT = 0;

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
	 * The lock object for network state updating.
	 */
	protected final Object stateLock = new Object();
	/**
	 * Network state of this channel.
	 */
	protected NetChannelState state = NetChannelState.CREATED;

	// --------------------------- Options fields ----------------------------

	/**
	 * Indicates whether to use multithreading to process request and response data when allowed (false by default).
	 */
	protected boolean multithreading = CONST_MULTITHREADING;
	/**
	 * The thread execution priority of this channel (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	protected int multithreadingPriority = CONST_MULTITHREADING_PRIORITY;
	/**
	 * The default timeout value in milliseconds that waiting for a response data callback (120000 ms by default).
	 */
	protected long defaultCallbackTimeout = CONST_CALLBACK_TIMEOUT;
	/**
	 * If no data is received within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	protected long receivingIdleTimeout = CONST_RECEIVING_IDLE_TIMEOUT;
	/**
	 * If no data is sent within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	protected long sendingIdleTimeout = CONST_SENDING_IDLE_TIMEOUT;

	// --------------------------- Private fields ----------------------------

	/**
	 * The inbounds and outbounds lock for the map.
	 */
	private final Object boundsLock = new Object();
	/**
	 * The inbounds map (the key is NetIO class, the value is inbound context object).
	 */
	private Map<Class<?>, NetInboundContext> inbounds;
	/**
	 * The outbounds map (the key is NetIO class, the value is outbound context object).
	 */
	private Map<Class<?>, NetOutboundContext> outbounds;
	/**
	 * The responser context to process asynchronous response callback message.
	 */
	private NetResponser responser;
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
		// Publish created event.
		NetEventFactory factory = service.getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(factory.createChannelEvent(this, this, NetChannelState.CREATED));
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public boolean config(NetChannelOptions options, boolean reset) {
		if (options == null) return false;

		// Reset configuration data.
		if (reset) {
			this.clearInbounds();
			this.clearOutbounds();
		}

		// Set configure data.
		multithreading = options.multithreading;
		multithreadingPriority = options.multithreadingPriority;
		defaultCallbackTimeout = options.defaultCallbackTimeout > 0 ? options.defaultCallbackTimeout : CONST_CALLBACK_TIMEOUT;
		receivingIdleTimeout = options.receivingIdleTimeout > 0 ? options.receivingIdleTimeout : CONST_RECEIVING_IDLE_TIMEOUT;
		sendingIdleTimeout = options.sendingIdleTimeout > 0 ? options.sendingIdleTimeout : CONST_SENDING_IDLE_TIMEOUT;

		// Setup inbounds.
		NetConfigInbound[] ins = options.inbounds;
		if (ins != null && ins.length > 0) {
			for (NetConfigInbound config : ins) {
				if (config == null || config.instance == null) continue;
				try {
					this.addInbound(IoTFramework.getInstance(config.instance), config.priority);
				} catch (Exception e) {
					FrameworkNet.getLogger().error(e);
					return false;
				}
			}
		}

		// Setup outbounds.
		NetConfigOutbound[] outs = options.outbounds;
		if (outs != null && outs.length > 0) {
			for (NetConfigOutbound config : outs) {
				if (config == null || config.instance == null) continue;
				try {
					this.addOutbound(IoTFramework.getInstance(config.instance), config.priority);
				} catch (Exception e) {
					FrameworkNet.getLogger().error(e);
					return false;
				}
			}
		}

		// Return configuration result.
		return true;
	}

	@Override
	public NetService getService() {
		return service;
	}

	@Override
	public String getChannelID() {
		return channelID;
	}

	@Override
	public boolean isMultithreading() {
		return multithreading;
	}

	@Override
	public int getMultithreadingPriority() {
		return multithreadingPriority;
	}

	@Override
	public long getDefaultCallbackTimeout() {
		return defaultCallbackTimeout;
	}

	@Override
	public long getReceivingIdleTimeout() {
		return receivingIdleTimeout;
	}

	@Override
	public long getSendingIdleTimeout() {
		return sendingIdleTimeout;
	}

	@Override
	public NetChannelState getState() {
		return state;
	}

	@Override
	public boolean isOpened() {
		return state == NetChannelState.OPENED;
	}

	@Override
	public boolean isClosed() {
		return state == NetChannelState.CLOSED;
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
		service.updateMessageTime(this, messageTime);
	}

	@Override
	public void updateSentTime() {
		sentTime = System.currentTimeMillis();
		service.updateSentTime(this, sentTime);
	}

	@Override
	public NetResponser getResponser() {
		if (responser != null) return responser;
		synchronized (boundsLock) {
			if (responser != null) return responser;
			responser = new NetResponser();
		}
		return responser;
	}

	@Override
	public long fixCallbackTimeout(long timeout) {
		return timeout <= 0 ? defaultCallbackTimeout : timeout;
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

	// --------------------------- Inbound and outbound methods ----------------------------

	@Override
	public void addInbound(NetInbound<?, ?> inbound, int priority) {
		if (inbound == null) return;
		if (inbounds == null) {
			synchronized (boundsLock) {
				if (inbounds == null) {
					inbounds = new HashMap<>();
				}
			}
		}
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
		if (inbound == null || inbounds == null) return;
		NetInboundContext context = inbounds.get(inbound.getIOClass());
		if (context != null) context.remove(inbound);
	}

	@Override
	public NetInboundObject[] getInbounds(Class<?> netIOClass) {
		if (inbounds == null) return null;
		NetInboundContext context = inbounds.get(netIOClass);
		return context == null ? null : context.getInbounds();
	}

	@Override
	public void clearInbounds() {
		if (inbounds == null) return;
		synchronized (inbounds) {
			inbounds.clear();
		}
	}

	@Override
	public void addOutbound(NetOutbound<?, ?> outbound, int priority) {
		if (outbound == null) return;
		if (outbounds == null) {
			synchronized (boundsLock) {
				if (outbounds == null) {
					outbounds = new HashMap<>();
				}
			}
		}
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
		if (outbound == null || outbounds == null) return;
		NetOutboundContext context = outbounds.get(outbound.getIOClass());
		if (context != null) context.remove(outbound);
	}

	@Override
	public NetOutboundObject[] getOutbounds(Class<?> netIOClass) {
		if (outbounds == null) return null;
		NetOutboundContext context = outbounds.get(netIOClass);
		return context == null ? null : context.getOutbounds();
	}

	@Override
	public void clearOutbounds() {
		if (outbounds == null) return;
		synchronized (outbounds) {
			outbounds.clear();
		}
	}

	// --------------------------- Open and close methods ----------------------------

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

			// Reset to created state after closing.
			if (state == NetChannelState.CLOSED) state = NetChannelState.CREATED;
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
			if (responser != null) responser.callbackOnClosing();
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
			if (responser != null) responser.callbackOnClosing();
			// Do close logic.
			try {
				doClose();
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
			state = NetChannelState.CLOSED;
			closeTime = System.currentTimeMillis();
			// Remove channel from service.
			service.removeChannel(channelID);
		}
	}

	@Override
	public void checkRunningStatus(long currentTime) {

		// Check the responses timeout.
		if (responser != null) responser.checkTimeout(currentTime);

		// Check reading timeout.
		boolean receiving = (receivingIdleTimeout > 0 && currentTime - messageTime > receivingIdleTimeout);
		// Check sending timeout.
		boolean sending = (sendingIdleTimeout > 0 && currentTime - sentTime > sendingIdleTimeout);

		// Close this channel on timeout.
		if (receiving || sending) {
			// Output message.
			if (receiving) {
				FrameworkNet.getLogger().info(FrameworkNet.getLocale().text("net.message.recv.idle", receivingIdleTimeout, channelID, messageTime, getClass().getName()));
			}
			if (sending) {
				FrameworkNet.getLogger().info(FrameworkNet.getLocale().text("net.message.send.idle", sendingIdleTimeout, channelID, sentTime, getClass().getName()));
			}
			// Close this channel.
			try {
				close();
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
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
