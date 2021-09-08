package org.iotcity.iot.framework.net.kafka;

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
	 * @param reader The kafka message reader object.
	 * @param sender The kafka message sender object.
	 */
	public NetKafkaIO(NetKafkaChannel<K, V> channel, NetKafkaReader<K, V> reader, NetKafkaSender<K, V> sender) {
		super(channel, reader, sender, true);
	}

}
