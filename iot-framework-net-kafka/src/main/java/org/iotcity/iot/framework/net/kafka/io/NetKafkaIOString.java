package org.iotcity.iot.framework.net.kafka.io;

import org.iotcity.iot.framework.net.io.NetIOHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaChannel;

/**
 * The kafka string key and string value I/O object.
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaIOString extends NetIOHandler<NetKafkaReader<String, String>, NetKafkaSender<String, String>> {

	/**
	 * Constructor for kafka string key and string value I/O object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param reader The kafka message reader object.
	 * @param sender The kafka message sender object.
	 */
	public NetKafkaIOString(NetKafkaChannel<String, String> channel, NetKafkaReader<String, String> reader, NetKafkaSender<String, String> sender) {
		super(channel, reader, sender, true);
	}

}
