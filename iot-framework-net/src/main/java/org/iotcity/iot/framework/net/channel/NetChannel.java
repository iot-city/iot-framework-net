package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetInbound;
import org.iotcity.iot.framework.net.io.NetOutbound;
import org.iotcity.iot.framework.net.io.NetResponser;

/**
 * The network channel.
 * @author ardon
 * @date 2021-06-16
 */
public interface NetChannel extends Configurable<NetChannelOptions> {

	/**
	 * Gets the service object of this channel (returns not null).
	 */
	NetService getService();

	/**
	 * Gets the channel unique identification (returns not null).
	 */
	String getChannelID();

	/**
	 * Indicates whether this channel is a client channel.
	 */
	boolean isClient();

	/**
	 * Indicates whether to use multithreading to process request and response data when allowed.
	 */
	boolean isMultithreading();

	/**
	 * Gets the thread execution priority of this channel (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	int getMultithreadingPriority();

	/**
	 * Gets the default timeout value in milliseconds that waiting for a response data callback (the default timeout value is 120000 ms).
	 */
	long getDefaultCallbackTimeout();

	/**
	 * Gets the receiving idle timeout, if no data is received within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	long getReceivingIdleTimeout();

	/**
	 * Gets the sending idle timeout, if no data is sent within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	long getSendingIdleTimeout();

	/**
	 * Indicates whether reopen the client channel after channel closing (false by default).
	 */
	boolean isReopenOnClosed();

	/**
	 * Gets the delayed time in milliseconds for automatically reopen the client channel after channel closing (5000 ms by default, the value must be greater than 0).
	 */
	long getReopenOnClosedDelay();

	/**
	 * Gets the current state of this channel.
	 */
	NetChannelState getState();

	/**
	 * Indicates whether this channel has been opened.
	 */
	boolean isOpened();

	/**
	 * Indicates whether this channel has been closed.
	 */
	boolean isClosed();

	/**
	 * Indicates whether this channel has been destroyed.
	 */
	boolean isDestroyed();

	/**
	 * Gets the create time of this channel in milliseconds.
	 */
	long getCreateTime();

	/**
	 * Gets the open time of this channel in milliseconds (returns 0 when not opened).
	 */
	long getOpenTime();

	/**
	 * Gets the close time of this channel in milliseconds (returns 0 when not closed).
	 */
	long getCloseTime();

	/**
	 * Gets the last time in milliseconds that this channel receives a message (returns 0 when no message is received).
	 */
	long getMessageTime();

	/**
	 * Gets the time in milliseconds that this channel last sent a message (returns 0 when no message is sent).
	 */
	long getSentTime();

	/**
	 * Update the time to the system time in milliseconds that this channel receives a message.
	 */
	void updateMessageTime();

	/**
	 * Update the time to the system time in milliseconds that this channel sent a message.
	 */
	void updateSentTime();

	/**
	 * Gets the responser to process asynchronous response callback message (returns not null).
	 */
	NetResponser getResponser();

	/**
	 * Fix the response callback timeout value by using the default callback timeout value of this channel.
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback.
	 * @return The timeout value in milliseconds that has been fixed.
	 */
	long fixCallbackTimeout(long timeout);

	/**
	 * Store a data object to this channel.
	 * @param data Data object that need to be stored in this channel.
	 */
	void setStoreData(Object data);

	/**
	 * Gets the data object that has been stored in this channel (returns null when there is no stored data.).
	 * @param <T> The data type of stored data.
	 * @return The data object that has been stored.
	 */
	<T> T getStoreData();

	/**
	 * Add inbound message processing object to this channel.
	 * @param inbound Network inbound message processing object.
	 * @param priority Inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	void addInbound(NetInbound<?, ?> inbound, int priority);

	/**
	 * Remove inbound message processing object from this channel.
	 * @param inbound Network inbound message processing object.
	 */
	void removeInbound(NetInbound<?, ?> inbound);

	/**
	 * Gets inbound message processing objects from this channel (returns null if there is no inbound for current I/O object).
	 * @param netIOClass The network I/O object class (extended from: {@link org.iotcity.iot.framework.net.io.NetIO }).
	 * @return Inbound message processing objects.
	 */
	NetInboundObject[] getInbounds(Class<?> netIOClass);

	/**
	 * Remove all inbound message processing objects from this channel.
	 */
	void clearInbounds();

	/**
	 * Add outbound message processing object to this channel.
	 * @param outbound Network outbound message processing object.
	 * @param priority Outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	void addOutbound(NetOutbound<?, ?> outbound, int priority);

	/**
	 * Remove outbound message processing object from this channel.
	 * @param outbound Network outbound message processing object.
	 */
	void removeOutbound(NetOutbound<?, ?> outbound);

	/**
	 * Gets outbound message processing objects from this channel (returns null if there is no outbound for current I/O object).
	 * @param netIOClass The network I/O object class (extended from: {@link org.iotcity.iot.framework.net.io.NetIO }).
	 * @return Outbound message processing objects.
	 */
	NetOutboundObject[] getOutbounds(Class<?> netIOClass);

	/**
	 * Remove all outbound message processing objects from this channel.
	 */
	void clearOutbounds();

	/**
	 * Open this channel, after successful opening, this channel will be automatically added to the service.
	 * @return Returns whether this channel was successfully opened.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean open() throws Exception;

	/**
	 * Close this channel, after successful closing, this channel will be automatically removed from the service.
	 * @return Returns whether this channel was successfully closed.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean close() throws Exception;

	/**
	 * Destroy this channel without closing event, calling this method will close this channel automatically.
	 */
	void destroy();

	/**
	 * Check the running status of this channel.
	 * @param currentTime Current system time.
	 */
	void checkRunningStatus(long currentTime);

	/**
	 * Gets the network I/O object that send a message to the remote end (returns null when this channel is not opened or this channel does not support sending request data to the remote end).
	 */
	NetIO<?, ?> getToRemoteIO();

}
