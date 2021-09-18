package org.iotcity.iot.framework.net.kafka.channels;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaChannel;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOString;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaReader;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSender;

/**
 * The kafka channel for message consumer and producer with String key and String value.
 * @author ardon
 * @date 2021-08-10
 */
public final class NetKafkaStringChannel extends NetKafkaChannel<String, String> {

	/**
	 * The lock for to remote I/O instance.
	 */
	private final Object lock = new Object();
	/**
	 * The kafka I/O object for remote requests.
	 */
	private NetKafkaIOString toRemoteIO;

	/**
	 * Constructor for kafka message channel with String key and String value.
	 * @param service The network service handler (required, can not be null).
	 * @param channelID Network channel unique identification (required, not null).
	 * @param consumerConfig The kafka consumer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @param producerConfig The kafka producer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "service", "channelID" is null or empty, or the parameter "consumerConfig" and "producerConfig" is invalid.
	 */
	public NetKafkaStringChannel(NetServiceHandler service, String channelID, NetKafkaConfigConsumer consumerConfig, NetKafkaConfigProducer producerConfig) throws IllegalArgumentException {
		super(service, channelID, consumerConfig, producerConfig);
		// Publish a created event.
		super.publishCreatedEvent();
	}

	@Override
	public final NetIO<?, ?> getToRemoteIO() {
		if (toRemoteIO != null) return toRemoteIO;
		// Gets the sender.
		NetKafkaSender<String, String> sender = this.getSender();
		if (sender == null) return null;
		// Create a to remote I/O object.
		synchronized (lock) {
			if (toRemoteIO != null) return toRemoteIO;
			toRemoteIO = new NetKafkaIOString(this, null, sender);
		}
		return toRemoteIO;
	}

	@Override
	protected final NetIO<?, ?> getFromRemoteIO(KafkaConsumer<String, String> consumer, ConsumerRecord<String, String> record) {
		return new NetKafkaIOString(this, new NetKafkaReader<>(consumer, record), this.getSender());
	}

}
