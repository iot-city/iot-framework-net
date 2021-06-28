package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Asynchronous response data for inbound and outbound.
 * @author ardon
 * @date 2021-06-25
 */
public class NetDataAsyncResponse extends NetDataResponse {

	/**
	 * The message queue key of the paired message request and response.
	 */
	private final String messageQueue;

	/**
	 * Constructor for asynchronous response data.
	 * @param messageQueue The message queue key of the paired message request and response.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageQueue" is null or empty.
	 */
	public NetDataAsyncResponse(String messageQueue) throws IllegalArgumentException {
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
