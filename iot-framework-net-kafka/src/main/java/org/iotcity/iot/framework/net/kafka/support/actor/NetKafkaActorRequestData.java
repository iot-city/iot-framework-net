package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;
import org.iotcity.iot.framework.net.support.actor.NetActorHeader;

/**
 * Kafka actor request data for network transmission.
 * @author ardon
 * @date 2021-09-07
 */
public final class NetKafkaActorRequestData implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * The actor header information (never null).
	 */
	public NetActorHeader header;
	/**
	 * The actor command information (never null).
	 */
	public NetActorCommand command;
	/**
	 * An array of parameters that be used to invoke the method (null if there is no parameter).
	 */
	public Serializable[] params;
	/**
	 * Request source information for response data callback (null when callback is not required).
	 */
	public NetKafkaTopicPartition callback;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor request data.
	 */
	public NetKafkaActorRequestData() {
	}

	/**
	 * Constructor for kafka actor request data.
	 * @param header The actor header information (required, can not be null).
	 * @param command The actor command information (required, can not be null).
	 * @param params An array of parameters that be used to invoke the method (optional, set it to null if there is no parameter).
	 * @param callback Request source information for response data callback (optional, set it to null when callback is not required).
	 */
	public NetKafkaActorRequestData(NetActorHeader header, NetActorCommand command, Serializable[] params, NetKafkaTopicPartition callback) {
		this.header = header;
		this.command = command;
		this.params = params;
		this.callback = callback;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{header=");
		sb.append(header.toString());
		sb.append(", command=");
		sb.append(command.toString());
		sb.append(", params=");
		JavaHelper.getArrayPreview(params, sb, false);
		sb.append(", callback=");
		sb.append(callback == null ? "null" : callback.toString());
		sb.append("}");
		return sb.toString();
	}

}
