package org.iotcity.iot.framework.net.support.actor;

import org.iotcity.iot.framework.net.io.NetDataAsyncResponse;

/**
 * Actor response for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorResponse extends NetDataAsyncResponse {

	/**
	 * The actor response data.
	 */
	private final NetActorResponseData data;

	/**
	 * Constructor for actor response.
	 * @param messageQueue The message queue key of the paired message request and response (required, can not be null or an empty string).
	 * @param data The actor response data (required, can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageQueue", "data" is null or empty.
	 */
	public NetActorResponse(String messageQueue, NetActorResponseData data) throws IllegalArgumentException {
		super(messageQueue);
		if (data == null) throw new IllegalArgumentException("Parameter data can not be null!");
		this.data = data;
	}

	/**
	 * Gets the actor response data (returns not null).
	 */
	public NetActorResponseData getData() {
		return data;
	}

}
