package org.iotcity.iot.framework.net.kafka;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetSenderHandler;

/**
 * The kafka message sender object.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @author ardon
 * @date 2021-09-04
 */
public final class NetKafkaSender<K, V> extends NetSenderHandler {

	/**
	 * The kafka channel for message consumer and producer.
	 */
	private final NetKafkaChannel<K, V> channel;
	/**
	 * A Kafka client that publishes records to the Kafka cluster.
	 */
	private final KafkaProducer<K, V> producer;

	/**
	 * Constructor for the kafka message sender object.
	 * @param channel The kafka channel for message consumer and producer.
	 * @param producer A Kafka client that publishes records to the Kafka cluster.
	 */
	NetKafkaSender(NetKafkaChannel<K, V> channel, KafkaProducer<K, V> producer) {
		this.channel = channel;
		this.producer = producer;
	}

	/**
	 * Gets the Kafka client that publishes records to the Kafka cluster (returns null if there is no producer in sender).
	 */
	public KafkaProducer<K, V> getProducer() {
		return producer;
	}

	/**
	 * Send a message to the Kafka cluster (returns not null).
	 * @param record A key/value pair to be sent to Kafka.
	 * @return The kafka sender result after execution (not null).
	 */
	public NetKafkaSenderResult send(ProducerRecord<K, V> record) {
		return send(record, null);
	}

	/**
	 * Send a message to the Kafka cluster (returns not null).
	 * @param record A key/value pair to be sent to Kafka.
	 * @param callback The status callback to execute when the record has been acknowledged by the server.
	 * @return The kafka sender result after execution (not null).
	 */
	public NetKafkaSenderResult send(ProducerRecord<K, V> record, NetKafkaSenderCallback callback) {
		if (producer == null) return new NetKafkaSenderResult(NetMessageStatus.NO_SENDER, null);
		Future<RecordMetadata> future = null;
		try {
			if (callback == null) {
				future = producer.send(record);
			} else {
				future = producer.send(record, new Callback() {

					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						// Logs error message.
						if (exception != null) channel.getLogger().error(exception);
						// Callback message result.
						callback.onCallback(exception == null ? NetMessageStatus.OK : NetMessageStatus.SEND_EXCEPTION, metadata, exception);
					}

				});
			}
			// Return accepted.
			return new NetKafkaSenderResult(NetMessageStatus.ACCEPTED, future);
		} catch (Exception e) {
			// Logs error message.
			channel.getLogger().error(e);
			// Return exception status.
			return new NetKafkaSenderResult(NetMessageStatus.SEND_EXCEPTION, future);
		}
	}

}
