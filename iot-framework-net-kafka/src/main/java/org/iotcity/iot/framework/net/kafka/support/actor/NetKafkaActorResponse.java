package org.iotcity.iot.framework.net.kafka.support.actor;

import org.iotcity.iot.framework.net.support.actor.NetActorResponse;
import org.iotcity.iot.framework.net.support.actor.NetActorResponseData;

/**
 * Kafka actor response for network transmission.
 * @author ardon
 * @date 2021-09-06
 */
public final class NetKafkaActorResponse extends NetActorResponse {

	/**
	 * The partition from which the message is received or sent.
	 */
	private final Integer partition;

	/**
	 * Constructor for kafka actor response.
	 * @param messageQueue The message queue key of the paired message request and response (required, can not be null or an empty string).
	 * @param data The actor response data (required, can not be null).
	 * @param partition The partition from which this message is received or sent (optional, set it to null if use the partition dynamically).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageQueue", "data" is null or empty.
	 */
	public NetKafkaActorResponse(String messageQueue, NetActorResponseData data, Integer partition) throws IllegalArgumentException {
		super(messageQueue, data);
		this.partition = partition;
	}

	/**
	 * The partition from which this message is received or sent (returns null if use the partition dynamically).
	 */
	public Integer getPartition() {
		return partition;
	}

}
