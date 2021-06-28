package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Asynchronous request data for inbound and outbound.
 * @author ardon
 * @date 2021-06-25
 */
public class NetDataAsyncRequest extends NetDataRequest {

	/**
	 * The message queue key of the paired message request and response.
	 */
	private final String messageQueue;

	/**
	 * Constructor for asynchronous request data.
	 * @param messageQueue The message queue key of the paired message request and response.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageQueue" is null or empty.
	 */
	public NetDataAsyncRequest(String messageQueue) throws IllegalArgumentException {
		if (StringHelper.isEmpty(messageQueue)) throw new IllegalArgumentException("Parameter messageQueue can not be null!");
		this.messageQueue = messageQueue;
	}

	/**
	 * Gets the message queue key of the paired message request and response (returns not null).
	 */
	public final String getMessageQueue() {
		return messageQueue;
	}

}
