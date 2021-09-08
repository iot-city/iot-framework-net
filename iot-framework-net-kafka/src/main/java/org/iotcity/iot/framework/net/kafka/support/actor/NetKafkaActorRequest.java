package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.kafka.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;
import org.iotcity.iot.framework.net.support.actor.NetActorHeader;
import org.iotcity.iot.framework.net.support.actor.NetActorRequest;

/**
 * Kafka actor request for network transmission.
 * @author ardon
 * @date 2021-09-06
 */
public final class NetKafkaActorRequest extends NetActorRequest {

	// --------------------------- Public fields ----------------------------

	/**
	 * The partition the message is received or sent to (never null).
	 */
	public final NetKafkaTopicPartition partition;
	/**
	 * Request source information for response data callback (null when callback is not required).
	 */
	public final NetKafkaTopicPartition callback;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor request (The I/O sender callback partition is in used).
	 * @param messageID The message ID of the paired message request and response (required, can not be null or an empty string).
	 * @param partition The partition the message is received or sent to (required, can not be null).
	 * @param header The actor header information (required, can not be null).
	 * @param command The actor command information (required, can not be null).
	 * @param params An array of parameters that be used to invoke the method (optional, set it to null if there is no parameter).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "messageID", "partition", "partition.topic", "header" or "command" is null or empty.
	 */
	public NetKafkaActorRequest(String messageID, NetKafkaTopicPartition partition, NetActorHeader header, NetActorCommand command, Serializable[] params) throws IllegalArgumentException {
		super(messageID, header, command, params);
		if (partition == null || StringHelper.isEmpty(partition.topic)) throw new IllegalArgumentException("Parameter partition and partition.topic can not be null or empty!");
		this.partition = partition;
		this.callback = null;
	}

	/**
	 * Constructor for kafka actor request.
	 * @param messageID The message ID of the paired message request and response (required, can not be null or an empty string).
	 * @param partition The partition the message is received or sent to (required, can not be null).
	 * @param header The actor header information (required, can not be null).
	 * @param command The actor command information (required, can not be null).
	 * @param params An array of parameters that be used to invoke the method (optional, set it to null if there is no parameter).
	 * @param callback Request source information for response data callback (optional, set it to null when use the I/O sender callback partition).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "messageID", "partition", "partition.topic", "header" or "command" is null or empty.
	 */
	public NetKafkaActorRequest(String messageID, NetKafkaTopicPartition partition, NetActorHeader header, NetActorCommand command, Serializable[] params, NetKafkaTopicPartition callback) throws IllegalArgumentException {
		super(messageID, header, command, params);
		if (partition == null || StringHelper.isEmpty(partition.topic)) throw new IllegalArgumentException("Parameter partition and partition.topic can not be null or empty!");
		this.partition = partition;
		this.callback = callback;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{messageID=\"");
		sb.append(messageID);
		sb.append("\", header=");
		sb.append(header.toString());
		sb.append(", command=");
		sb.append(command.toString());
		sb.append(", params=");
		JavaHelper.getArrayPreview(params, sb, false);
		sb.append(", partition=");
		sb.append(partition.toString());
		sb.append(", callback=");
		sb.append(callback == null ? "null" : callback.toString());
		sb.append("}");
		return sb.toString();
	}

}
