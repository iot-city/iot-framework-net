package org.iotcity.iot.framework.net.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.iotcity.iot.framework.net.io.NetIOHandler;

/**
 * @author ardon
 * @date 2021-06-19
 */
public class NetKafkaIO<K, V> extends NetIOHandler<ConsumerRecord<K, V>, KafkaProducer<K, V>> {

	public NetKafkaIO(NetKafkaChannel<K, V> channel, ConsumerRecord<K, V> reader, KafkaProducer<K, V> sender) {
		super(channel, reader, sender, true);
	}

}
