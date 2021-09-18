package org.iotcity.iot.framework.net.kafka.io;

import java.io.Serializable;

/**
 * Kafka topic name and partition number information.
 * @author ardon
 * @date 2021-09-08
 */
public final class NetKafkaTopicPartition implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * The topic name (never null).
	 */
	public String topic;
	/**
	 * The partition number of topic (null when use the dynamically assigned partition).
	 */
	public Integer partition;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka topic name and partition number information.
	 */
	public NetKafkaTopicPartition() {
	}

	/**
	 * Constructor for kafka topic name and partition number information.
	 * @param topic The topic name (required, can not be null or empty).
	 * @param partition The partition number of topic (optional, set it to null when use the dynamically assigned partition).
	 */
	public NetKafkaTopicPartition(String topic, Integer partition) {
		this.topic = topic;
		this.partition = partition;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{topic=\"");
		sb.append(topic);
		sb.append("\", partition=");
		sb.append(partition);
		sb.append("}");
		return sb.toString();
	}

}
