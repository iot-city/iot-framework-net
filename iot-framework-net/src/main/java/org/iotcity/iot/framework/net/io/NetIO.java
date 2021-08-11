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
	 * Determines whether to use multithreading to process request and response data when allowed.
	 */
	boolean isMultithreading();

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
	 * Gets the responser to process asynchronous response callback message (returns not null).
	 */
	NetResponser getResponser();

	/**
	 * Gets the reader object to read messages (returns null if there is no reader for current I/O object, e.g. when the message is asynchronous requesting to remote, null value will be returned).
	 */
	READER getReader();

	/**
	 * Gets the sender object to send messages (returns null if there is no sender for current I/O object).
	 */
	SENDER getSender();

}
