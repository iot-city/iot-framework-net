package org.iotcity.iot.framework.net.support.actor;

import org.iotcity.iot.framework.net.io.NetDataAsyncRequest;

/**
 * Actor request for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorRequest extends NetDataAsyncRequest {

	/**
	 * The actor request data.
	 */
	private final NetActorRequestData data;

	/**
	 * Constructor for actor request.
	 * @param messageQueue The message queue key of the paired message request and response (required, can not be null or an empty string).
	 * @param data The actor request data (required, can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageQueue" or "data" is null or empty.
	 */
	public NetActorRequest(String messageQueue, NetActorRequestData data) throws IllegalArgumentException {
		super(messageQueue);
		if (data == null) throw new IllegalArgumentException("Parameter data can not be null!");
		this.data = data;
	}

	/**
	 * Gets the actor request data (returns not null).
	 */
	public NetActorRequestData getData() {
		return data;
	}

}
