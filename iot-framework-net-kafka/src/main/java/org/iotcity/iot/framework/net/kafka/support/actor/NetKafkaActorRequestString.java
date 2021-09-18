package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;
import org.iotcity.iot.framework.net.support.actor.NetActorHeader;

/**
 * Kafka actor request string data for network transmission.
 * @author ardon
 * @date 2021-09-12
 */
public class NetKafkaActorRequestString implements Serializable {

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
	public String params;
	/**
	 * Request source information for response data callback (null when callback is not required).
	 */
	public NetKafkaTopicPartition callback;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor request string data.
	 */
	public NetKafkaActorRequestString() {
	}

	/**
	 * Constructor for kafka actor request string data.
	 * @param header The actor header information (required, can not be null).
	 * @param command The actor command information (required, can not be null).
	 * @param params An array of parameters that be used to invoke the method (optional, set it to null if there is no parameter).
	 * @param callback Request source information for response data callback (optional, set it to null when callback is not required).
	 */
	public NetKafkaActorRequestString(NetActorHeader header, NetActorCommand command, String params, NetKafkaTopicPartition callback) {
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
		JavaHelper.getDataPreview(params, sb);
		sb.append(", callback=");
		sb.append(callback == null ? "null" : callback.toString());
		sb.append("}");
		return sb.toString();
	}

}
