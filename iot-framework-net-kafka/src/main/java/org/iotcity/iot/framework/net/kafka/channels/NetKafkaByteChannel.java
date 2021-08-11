package org.iotcity.iot.framework.net.kafka.channels;

import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaChannel;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;

/**
 * The kafka channel for message consumer and producer with byte[] key and byte[] value.
 * @author ardon
 * @date 2021-08-10
 */
public final class NetKafkaByteChannel extends NetKafkaChannel<byte[], byte[]> {

	/**
	 * Constructor for kafka message channel with byte[] key and byte[] value.
	 * @param service The network service handler (required, can not be null).
	 * @param channelID Network channel unique identification (required, not null).
	 * @param consumerConfig The kafka consumer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @param producerConfig The kafka producer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "service", "channelID" is null or empty, or the parameter "consumerConfig" and "producerConfig" is invalid.
	 */
	public NetKafkaByteChannel(NetServiceHandler service, String channelID, NetKafkaConfigConsumer consumerConfig, NetKafkaConfigProducer producerConfig) throws IllegalArgumentException {
		super(service, channelID, consumerConfig, producerConfig);
	}

}
