package org.iotcity.iot.framework.net.kafka.io;

import org.iotcity.iot.framework.net.io.NetIOHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaChannel;

/**
 * The kafka bytes key and bytes value data input and output object.
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaIOBytes extends NetIOHandler<NetKafkaReader<byte[], byte[]>, NetKafkaSender<byte[], byte[]>> {

	/**
	 * Constructor for kafka bytes key and bytes value I/O object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param reader The kafka message reader object.
	 * @param sender The kafka message sender object.
	 */
	public NetKafkaIOBytes(NetKafkaChannel<byte[], byte[]> channel, NetKafkaReader<byte[], byte[]> reader, NetKafkaSender<byte[], byte[]> sender) {
		super(channel, reader, sender, true);
	}

}
