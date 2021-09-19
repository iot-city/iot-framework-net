package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.support.actor.NetActorResponse;
import org.iotcity.iot.framework.net.support.actor.NetActorResult;

/**
 * Kafka actor response for network transmission.
 * @author ardon
 * @date 2021-09-06
 */
public final class NetKafkaActorResponse extends NetActorResponse {

	// --------------------------- Public fields ----------------------------

	/**
	 * The partition the message is received or sent to (never null).
	 */
	public final NetKafkaTopicPartition partition;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor response.
	 * @param messageID The message ID of the paired message request and response (required, can not be null or an empty string).
	 * @param partition The partition the message is received or sent to (required, can not be null).
	 * @param result Actor response status result (required, can not be null).
	 * @param data The actor response data from actor method invoking (optional, set it to null if there is no response data).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "messageID", "partition", "partition.topic" or "result" is null or empty.
	 */
	public NetKafkaActorResponse(String messageID, NetKafkaTopicPartition partition, NetActorResult result, Serializable data) throws IllegalArgumentException {
		super(messageID, result, data);
		if (partition == null || StringHelper.isEmpty(partition.topic)) throw new IllegalArgumentException("Parameter partition and partition.topic can not be null or empty!");
		this.partition = partition;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{messageID=\"");
		sb.append(messageID);
		sb.append("\", result=");
		sb.append(result);
		sb.append(", data=");
		JavaHelper.getDataPreview(data, sb);
		sb.append(", partition=");
		sb.append(partition);
		sb.append("}");
		return sb.toString();
	}

}
