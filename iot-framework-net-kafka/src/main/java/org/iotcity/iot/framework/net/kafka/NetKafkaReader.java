package org.iotcity.iot.framework.net.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.iotcity.iot.framework.net.io.NetReaderHandler;

/**
 * The kafka message reader object.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @author ardon
 * @date 2021-09-04
 */
public final class NetKafkaReader<K, V> extends NetReaderHandler {

	/**
	 * A client that consumes records from a Kafka cluster.
	 */
	private final KafkaConsumer<K, V> consumer;
	/**
	 * A key/value pair to be received from Kafka.
	 */
	private final ConsumerRecord<K, V> record;

	/**
	 * Constructor for kafka message reader object.
	 * @param consumer A client that consumes records from a Kafka cluster.
	 * @param record A key/value pair to be received from Kafka.
	 */
	NetKafkaReader(KafkaConsumer<K, V> consumer, ConsumerRecord<K, V> record) {
		this.consumer = consumer;
		this.record = record;
	}

	/**
	 * Gets the client that consumes records from a Kafka cluster.
	 */
	public KafkaConsumer<K, V> getConsumer() {
		return consumer;
	}

	/**
	 * Gets the key/value pair to be received from Kafka.
	 */
	public ConsumerRecord<K, V> getRecord() {
		return record;
	}

}
