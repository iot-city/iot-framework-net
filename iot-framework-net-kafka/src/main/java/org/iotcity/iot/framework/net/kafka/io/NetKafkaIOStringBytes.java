package org.iotcity.iot.framework.net.kafka.io;

import org.iotcity.iot.framework.net.io.NetIOHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaChannel;

/**
 * The kafka string key and bytes value I/O object.
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaIOStringBytes extends NetIOHandler<NetKafkaReader<String, byte[]>, NetKafkaSender<String, byte[]>> {

	/**
	 * Constructor for kafka string key and bytes value I/O object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param reader The kafka message reader object.
	 * @param sender The kafka message sender object.
	 */
	public NetKafkaIOStringBytes(NetKafkaChannel<String, byte[]> channel, NetKafkaReader<String, byte[]> reader, NetKafkaSender<String, byte[]> sender) {
		super(channel, reader, sender, true);
	}

}
