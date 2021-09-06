package org.iotcity.iot.framework.net.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.iotcity.iot.framework.net.io.NetIOHandler;

/**
 * The kafka network input and output object.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @author ardon
 * @date 2021-06-19
 */
public final class NetKafkaIO<K, V> extends NetIOHandler<NetKafkaReader<K, V>, NetKafkaSender<K, V>> {

	/**
	 * Constructor for kafka I/O object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param consumer A client that consumes records from a Kafka cluster.
	 * @param record A key/value pair to be received from Kafka.
	 * @param producer A kafka client that publishes records to the kafka cluster.
	 */
	public NetKafkaIO(NetKafkaChannel<K, V> channel, KafkaConsumer<K, V> consumer, ConsumerRecord<K, V> record, KafkaProducer<K, V> producer) {
		super(channel, new NetKafkaReader<>(consumer, record), new NetKafkaSender<>(channel, producer), true);
	}

}
