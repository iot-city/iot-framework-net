package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.net.io.NetIO;

/**
 * The network channel.
 * @author ardon
 * @date 2021-06-16
 */
public interface NetChannel {

	/**
	 * Gets the service object of this channel (returns not null).
	 */
	NetService getService();

	/**
	 * Gets the channel unique identification (returns not null).
	 */
	String getChannelID();

	/**
	 * Gets the current state of this channel.
	 */
	NetChannelState getState();

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
	 * Open this channel.
	 * @return Returns whether this channel was successfully opened.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean open() throws Exception;

	/**
	 * Close this channel.
	 * @return Returns whether this channel was successfully closed.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean close() throws Exception;

	/**
	 * Gets the network I/O object that send a message to the remote end (returns null when this channel is not opened or this channel does not support sending request data to the remote end).
	 */
	NetIO<?, ?, ?> getToRemoteIO();

}
