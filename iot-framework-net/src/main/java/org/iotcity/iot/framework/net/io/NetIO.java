package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetInboundObject;
import org.iotcity.iot.framework.net.channel.NetOutboundObject;
import org.iotcity.iot.framework.net.channel.NetService;

/**
 * The network input and output object.
 * @param <READER> Reader class type.
 * @param <SENDER> Sender class type.
 * @author ardon
 * @date 2021-06-17
 */
public interface NetIO<READER, SENDER> {

	/**
	 * Gets the network service of this I/O object (returns not null).
	 */
	NetService getService();

	/**
	 * Gets the network channel of this I/O object (returns not null).
	 */
	NetChannel getChannel();

	/**
	 * Indicates whether the asynchronous processing mode is used.
	 */
	boolean isAsynchronous();

	/**
	 * Gets inbound message processing objects of this I/O object (returns null if there is no inbound for current I/O object).
	 * @return Inbound message processing objects.
	 */
	NetInboundObject[] getInbounds();

	/**
	 * Gets outbound message processing objects of this I/O object (returns null if there is no outbound for current I/O object).
	 * @return Outbound message processing objects.
	 */
	NetOutboundObject[] getOutbounds();

	/**
	 * Gets the responser to process asynchronous response callback message.
	 */
	NetResponser getResponser();

	/**
	 * Gets the reader object to read messages.
	 */
	READER getReader();

	/**
	 * Gets the sender object to send messages.
	 */
	SENDER getSender();

}
