package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannel;

/**
 * The network input and output object.
 * @param <CHANNEL> Network channel class type.
 * @param <READER> Reader class type.
 * @param <SENDER> Sender class type.
 * @author ardon
 * @date 2021-06-17
 */
public interface NetIO<CHANNEL extends NetChannel, READER, SENDER> {

	/**
	 * Gets the network channel object.
	 */
	CHANNEL getChannel();

	/**
	 * Gets the reader object to read messages.
	 */
	READER getReader();

	/**
	 * Gets the sender object to send messages.
	 */
	SENDER getSender();

}
