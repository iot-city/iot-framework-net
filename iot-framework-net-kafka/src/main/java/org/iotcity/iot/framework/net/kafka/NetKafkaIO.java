package org.iotcity.iot.framework.net.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.iotcity.iot.framework.net.io.NetIOHandler;

/**
 * The kafka I/O object class.
 * @param <K> The kafka message key type.
 * @param <K> The kafka message value type.
 * @author ardon
 * @date 2021-06-19
 */
public class NetKafkaIO<K, V> extends NetIOHandler<ConsumerRecord<K, V>, KafkaProducer<K, V>> {

	/**
	 * Constructor for kafka I/O object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param reader A client that consumes records from a kafka cluster.
	 * @param sender A kafka client that publishes records to the kafka cluster.
	 */
	public NetKafkaIO(NetKafkaChannel<K, V> channel, ConsumerRecord<K, V> reader, KafkaProducer<K, V> sender) {
		super(channel, reader, sender, true);
	}

}
