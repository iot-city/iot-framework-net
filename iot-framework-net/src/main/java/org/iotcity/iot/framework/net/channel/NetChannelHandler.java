package org.iotcity.iot.framework.net.channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.PriorityRunnable;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
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
	/**
	 * The default value for channel reopen options.
	 */
	public static final boolean CONST_REOPEN_ON_CLOSED = false;
	/**
	 * The default value for delayed time in milliseconds for automatically reopen the channel after channel closing.
	 */
	public static final long CONST_REOPEN_ON_CLOSED_DELAY = 5000;

	// --------------------------- Protected fields ----------------------------

	/**
	 * The logger object for this channel.
	 */
	protected final Logger logger;
	/**
	 * The locale text object for this channel.
	 */
	protected final LocaleText locale;
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
	private boolean multithreading = CONST_MULTITHREADING;
	/**
	 * The thread execution priority of this channel (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	private int multithreadingPriority = CONST_MULTITHREADING_PRIORITY;
	/**
	 * The default timeout value in milliseconds that waiting for a response data callback (120000 ms by default).
	 */
	private long defaultCallbackTimeout = CONST_CALLBACK_TIMEOUT;
	/**
	 * If no data is received within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	private long receivingIdleTimeout = CONST_RECEIVING_IDLE_TIMEOUT;
	/**
	 * If no data is sent within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	private long sendingIdleTimeout = CONST_SENDING_IDLE_TIMEOUT;
	/**
	 * Indicates whether reopen the client channel after channel closing (false by default).
	 */
	private boolean reopenOnClosed = CONST_REOPEN_ON_CLOSED;
	/**
	 * The delayed time in milliseconds for automatically reopen the client channel after channel closing (5000 ms by default, the value must be greater than 0).
	 */
	private long reopenOnClosedDelay = CONST_REOPEN_ON_CLOSED_DELAY;

	// --------------------------- Private fields ----------------------------

	/**
	 * Indicates whether this channel is a client channel.
	 */
	private final boolean client;
	/**
	 * The create time of current channel in milliseconds.
	 */
	private final long createTime;
	/**
	 * The reopen task ID.
	 */
	private final AtomicLong reopenTaskID = new AtomicLong(0);
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
	 * Indicates whether this channel has been destroyed.
	 */
	private boolean destroyed = false;
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
	 * @param isClient Indicates whether this channel is a client channel.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "service" or "channelID" is null or empty.
	 */
	protected NetChannelHandler(NetServiceHandler service, String channelID, boolean isClient) throws IllegalArgumentException {
		if (service == null || StringHelper.isEmpty(channelID)) {
			throw new IllegalArgumentException("Parameter service and channelID can not be null or empty!");
		}
		this.logger = service.logger;
		this.locale = service.locale;
		this.service = service;
		this.channelID = channelID;
		this.client = isClient;
		this.createTime = System.currentTimeMillis();
		// Publish created event.
		NetEventFactory factory = service.getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(factory.createChannelEvent(this, this, NetChannelState.CREATED));
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final boolean config(NetChannelOptions data, boolean reset) {
		// Returns true if there is no options data.
		if (data == null) return true;

		// Reset configuration data.
		if (reset) {
			this.clearInbounds();
			this.clearOutbounds();
		}

		// Set configure data.
		multithreading = data.multithreading;
		multithreadingPriority = data.multithreadingPriority;
		defaultCallbackTimeout = data.defaultCallbackTimeout > 0 ? data.defaultCallbackTimeout : CONST_CALLBACK_TIMEOUT;
		receivingIdleTimeout = data.receivingIdleTimeout > 0 ? data.receivingIdleTimeout : CONST_RECEIVING_IDLE_TIMEOUT;
		sendingIdleTimeout = data.sendingIdleTimeout > 0 ? data.sendingIdleTimeout : CONST_SENDING_IDLE_TIMEOUT;
		reopenOnClosed = client && data.reopenOnClosed;
		reopenOnClosedDelay = data.reopenOnClosedDelay > 0 ? data.reopenOnClosedDelay : CONST_REOPEN_ON_CLOSED_DELAY;

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
		return true;
	}

	@Override
	public final NetService getService() {
		return service;
	}

	@Override
	public final String getChannelID() {
		return channelID;
	}

	@Override
	public final boolean isClient() {
		return client;
	}

	@Override
	public final boolean isMultithreading() {
		return multithreading;
	}

	@Override
	public final int getMultithreadingPriority() {
		return multithreadingPriority;
	}

	@Override
	public final long getDefaultCallbackTimeout() {
		return defaultCallbackTimeout;
	}

	@Override
	public final long getReceivingIdleTimeout() {
		return receivingIdleTimeout;
	}

	@Override
	public final long getSendingIdleTimeout() {
		return sendingIdleTimeout;
	}

	@Override
	public final boolean isReopenOnClosed() {
		return reopenOnClosed;
	}

	@Override
	public final long getReopenOnClosedDelay() {
		return reopenOnClosedDelay;
	}

	@Override
	public final NetChannelState getState() {
		return state;
	}

	@Override
	public final boolean isOpened() {
		return state == NetChannelState.OPENED;
	}

	@Override
	public final boolean isClosed() {
		return state == NetChannelState.CLOSED;
	}

	@Override
	public final boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public final long getCreateTime() {
		return createTime;
	}

	@Override
	public final long getOpenTime() {
		return openTime;
	}

	@Override
	public final long getCloseTime() {
		return closeTime;
	}

	@Override
	public final long getMessageTime() {
		return messageTime;
	}

	@Override
	public final long getSentTime() {
		return sentTime;
	}

	@Override
	public final void updateMessageTime() {
		messageTime = System.currentTimeMillis();
		service.updateMessageTime(this, messageTime);
	}

	@Override
	public final void updateSentTime() {
		sentTime = System.currentTimeMillis();
		service.updateSentTime(this, sentTime);
	}

	@Override
	public final NetResponser getResponser() {
		if (responser != null) return responser;
		synchronized (boundsLock) {
			if (responser != null) return responser;
			responser = new NetResponser();
		}
		return responser;
	}

	@Override
	public final long fixCallbackTimeout(long timeout) {
		return timeout <= 0 ? defaultCallbackTimeout : timeout;
	}

	@Override
	public final void setStoreData(Object data) {
		storedData = data;
	}

	@Override
	public final <T> T getStoreData() {
		@SuppressWarnings("unchecked")
		T data = (T) storedData;
		return data;
	}

	// --------------------------- Inbound and outbound methods ----------------------------

	@Override
	public final void addInbound(NetInbound<?, ?> inbound, int priority) {
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
	public final void removeInbound(NetInbound<?, ?> inbound) {
		if (inbound == null || inbounds == null) return;
		NetInboundContext context = inbounds.get(inbound.getIOClass());
		if (context != null) context.remove(inbound);
	}

	@Override
	public final NetInboundObject[] getInbounds(Class<?> netIOClass) {
		if (inbounds == null) return null;
		NetInboundContext context = inbounds.get(netIOClass);
		return context == null ? null : context.getInbounds();
	}

	@Override
	public final void clearInbounds() {
		if (inbounds == null) return;
		synchronized (inbounds) {
			inbounds.clear();
		}
	}

	@Override
	public final void addOutbound(NetOutbound<?, ?> outbound, int priority) {
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
	public final void removeOutbound(NetOutbound<?, ?> outbound) {
		if (outbound == null || outbounds == null) return;
		NetOutboundContext context = outbounds.get(outbound.getIOClass());
		if (context != null) context.remove(outbound);
	}

	@Override
	public final NetOutboundObject[] getOutbounds(Class<?> netIOClass) {
		if (outbounds == null) return null;
		NetOutboundContext context = outbounds.get(netIOClass);
		return context == null ? null : context.getOutbounds();
	}

	@Override
	public final void clearOutbounds() {
		if (outbounds == null) return;
		synchronized (outbounds) {
			outbounds.clear();
		}
	}

	// --------------------------- Open and close methods ----------------------------

	@Override
	public boolean open() throws Exception {
		if (service.isStopped()) return false;
		if (state == NetChannelState.OPENED) return true;
		synchronized (stateLock) {
			if (state == NetChannelState.OPENED) return true;

			// Logs a message.
			logger.info(locale.text("net.service.channel.opening", channelID, service.serviceID));

			// Publish opening event.
			NetEventFactory factory = service.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetChannelEvent event = factory.createChannelEvent(this, this, NetChannelState.OPENING);
			if (publisher.publish(event).isCancelled()) {
				logger.warn(locale.text("net.service.channel.opening.cancelled", channelID, service.serviceID));
				return false;
			}

			// Reset to created state after closing.
			if (state == NetChannelState.CLOSED) state = NetChannelState.CREATED;
			// Reset destroyed status.
			if (destroyed) destroyed = false;

			try {
				// Do open logic.
				if (!doOpen()) {
					// Logs a message.
					logger.warn(locale.text("net.service.channel.open.failed", channelID, service.serviceID));
					// Retry opening this channel.
					retryOpen();
					// Return false at this time.
					return false;
				}
			} catch (Exception e) {
				// Logs a message.
				logger.error(locale.text("net.service.channel.open.err", channelID, service.serviceID, e.getMessage()), e);
				// Retry opening this channel.
				retryOpen();
				// Return false at this time.
				return false;
			}

			// Set opened state.
			state = NetChannelState.OPENED;
			openTime = System.currentTimeMillis();
			// Add channel to service
			if (service.addChannel(this)) {
				// Logs a message.
				logger.info(locale.text("net.service.channel.open.success", channelID, service.serviceID));
				// Publish opened event.
				publisher.publish(factory.createChannelEvent(this, this, NetChannelState.OPENED));
			} else {
				// Logs a message.
				logger.warn(locale.text("net.service.channel.addion.failed", channelID, service.serviceID));
				// Close the channel after adding failure.
				this.close();
			}

		}
		// Return opened successfully.
		return true;
	}

	@Override
	public boolean close() throws Exception {
		if (state == NetChannelState.CLOSED) return true;
		synchronized (stateLock) {
			if (state == NetChannelState.CLOSED) return true;

			// Logs a message.
			logger.info(locale.text("net.service.channel.closing", channelID, service.serviceID));

			// Publish closing event.
			NetEventFactory factory = service.getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			NetChannelEvent event = factory.createChannelEvent(this, this, NetChannelState.CLOSING);
			if (publisher.publish(event).isCancelled()) {
				logger.warn(locale.text("net.service.channel.closing.cancelled", channelID, service.serviceID));
				return false;
			}

			try {
				// Do close logic.
				if (!doClose()) {
					logger.warn(locale.text("net.service.channel.close.failed", channelID, service.serviceID));
					return false;
				}
			} catch (Exception e) {
				logger.error(locale.text("net.service.channel.close.err", channelID, service.serviceID, e.getMessage()), e);
				return false;
			}

			// Set close state.
			state = NetChannelState.CLOSED;
			closeTime = System.currentTimeMillis();
			if (responser != null && !reopenOnClosed) responser.callbackOnClosing();
			service.removeChannel(this);

			// Logs a message.
			logger.info(locale.text("net.service.channel.close.success", channelID, service.serviceID));
			// Publish closed event.
			publisher.publish(factory.createChannelEvent(this, this, NetChannelState.CLOSED));

			// Retry opening this channel.
			retryOpen();

		}
		// Return closed successfully.
		return true;
	}

	@Override
	public void destroy() {
		if (destroyed) return;
		synchronized (stateLock) {
			if (destroyed) return;

			// Logs a message.
			logger.info(locale.text("net.service.channel.destroying", channelID, service.serviceID));

			// Check for close state.
			if (state != NetChannelState.CLOSED) {

				try {
					// Do close logic.
					if (!doClose()) {
						logger.warn(locale.text("net.service.channel.close.failed", channelID, service.serviceID));
					} else {
						logger.info(locale.text("net.service.channel.close.success", channelID, service.serviceID));
					}
				} catch (Exception e) {
					logger.error(locale.text("net.service.channel.close.err", channelID, service.serviceID, e.getMessage()), e);
				}

				// Set close state.
				state = NetChannelState.CLOSED;
				closeTime = System.currentTimeMillis();
				if (responser != null) responser.callbackOnClosing();
				service.removeChannel(this);
				// Publish closed event.
				IoTFramework.getBusEventPublisher().publish(service.getEventFactory().createChannelEvent(this, this, NetChannelState.CLOSED));

			}

			// Set destroyed status.
			destroyed = true;
			// Gets the reopen task ID.
			long taskID = reopenTaskID.getAndSet(0);
			// Remove the reopen task.
			if (taskID > 0) TaskHandler.getDefaultHandler().remove(taskID);
			// Logs a message.
			logger.info(locale.text("net.service.channel.destroyed", channelID, service.serviceID));

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
				logger.warn(locale.text("net.service.channel.recv.idle", receivingIdleTimeout, channelID, service.serviceID, messageTime, getClass().getName()));
			}
			if (sending) {
				logger.warn(locale.text("net.service.channel.send.idle", sendingIdleTimeout, channelID, service.serviceID, sentTime, getClass().getName()));
			}
			// Close this channel.
			try {
				close();
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	/**
	 * Retry opening this channel.
	 */
	private final void retryOpen() {
		// Check reopen conditions.
		if (!reopenOnClosed || destroyed || service.isStopped() || reopenTaskID.get() > 0) return;
		// Logs a message.
		logger.info(locale.text("net.service.channel.open.retry", reopenOnClosedDelay, channelID, service.serviceID));
		// Reopen this channel.
		reopenTaskID.set(TaskHandler.getDefaultHandler().addDelayTask(new PriorityRunnable() {

			@Override
			public void run() {
				// Reset task ID
				reopenTaskID.set(0);
				synchronized (stateLock) {
					// Check status.
					if (destroyed || service.isStopped()) return;
				}
				// Logs a message.
				logger.info(locale.text("net.service.channel.reopening", channelID, service.serviceID));
				try {
					// Do reopen logic.
					doReopen();
				} catch (Exception e) {
					// Logs a message.
					logger.error(locale.text("net.service.channel.reopening.err", channelID, service.serviceID, e.getMessage()), e);
				}
			}

		}, reopenOnClosedDelay));
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Do open channel processing logic.
	 * @return Returns whether the channel was successfully opened.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doOpen() throws Exception;

	/**
	 * Do reopen channel processing logic (you can call open() in this method).
	 * @return Returns whether the channel was successfully opened.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doReopen() throws Exception;

	/**
	 * Do close channel processing logic.
	 * @return Returns whether the channel was successfully closed.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	protected abstract boolean doClose() throws Exception;

}
