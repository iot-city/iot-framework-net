package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Asynchronous response data for inbound and outbound.
 * @author ardon
 * @date 2021-06-25
 */
public class NetDataAsyncResponse extends NetDataResponse {

	/**
	 * The message ID of the paired message request and response.
	 */
	protected final String messageID;

	/**
	 * Constructor for asynchronous response data.
	 * @param messageID The message ID of the paired message request and response.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageID" is null or empty.
	 */
	public NetDataAsyncResponse(String messageID) throws IllegalArgumentException {
		if (StringHelper.isEmpty(messageID)) throw new IllegalArgumentException("Parameter messageID can not be null or empty!");
		this.messageID = messageID;
	}

	/**
	 * Gets the message ID of the paired message request and response (returns not null).
	 */
	public final String getMessageID() {
		return messageID;
	}

}
