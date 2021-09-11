package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetInboundObject;
import org.iotcity.iot.framework.net.channel.NetOutboundObject;
import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.support.actor.NetActorClassFactory;

/**
 * The network input and output object.
 * @param <READER> Reader class type.
 * @param <SENDER> Sender class type.
 * @author ardon
 * @date 2021-06-17
 */
public interface NetIO<READER extends NetReader, SENDER extends NetSender> {

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
	 * Gets the request data from responser in asynchronous callback mode (returns null if not found).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param messageID The message ID of the paired message request and response (required, can not be null or empty).
	 * @param requestClass The request data class (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @return The request data object.
	 */
	<REQ extends NetDataRequest, RES extends NetDataResponse> REQ getCallbackRequest(String messageID, Class<REQ> requestClass, Class<RES> responseClass);

	/**
	 * Gets the request data currently sent to remote (returns null if not found).
	 * @param <REQ> The request data type.
	 * @return The request data object.
	 */
	<REQ extends NetDataRequest> REQ getToRemoteRequest();

	/**
	 * Gets the responser to process asynchronous response callback message (returns not null).
	 */
	NetResponser getResponser();

	/**
	 * Gets the class factory instance for inbound or outbound serialization (returns null if there is no class factory).
	 * @param <T> The type of class factory, e.g. {@link NetActorClassFactory }.
	 * @return The class factory instance.
	 */
	<T> T getClassFactory();

	/**
	 * Gets the reader object to read messages (returns null if there is no reader for current I/O object, e.g. when the message is asynchronous requesting to remote, null value will be returned).
	 */
	READER getReader();

	/**
	 * Gets the sender object to send messages (returns null if there is no sender for current I/O object).
	 */
	SENDER getSender();

}
