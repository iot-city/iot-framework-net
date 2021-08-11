package org.iotcity.iot.framework.net.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.WakeupException;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannelHandler;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;

/**
 * The kafka channel for message consumer and producer.
 * @param <K> The kafka message key type.
 * @param <K> The kafka message value type.
 * @author ardon
 * @date 2021-06-16
 */
public class NetKafkaChannel<K, V> extends NetChannelHandler {

	/**
	 * Subscribe to the specified topics (if set it to null or empty elements, it will use the pattern regex string to subscribe to).
	 */
	private final String[] topics;
	/**
	 * Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions.
	 */
	private final String pattern;
	/**
	 * The time, in milliseconds, spent waiting in poll if data is not available in the buffer (100 by default).<br/>
	 * If 0, returns immediately with any records that are available currently in the buffer, else returns empty. Must not be negative.
	 */
	private final long pollTimeout;
	/**
	 * A client that consumes records from a kafka cluster.
	 */
	private final KafkaConsumer<K, V> consumer;
	/**
	 * A kafka client that publishes records to the kafka cluster.
	 */
	private final KafkaProducer<K, V> producer;
	/**
	 * The consumer runnable object.
	 */
	private ConsumerRunnable consumerRunnable;

	/**
	 * Constructor for kafka message channel.
	 * @param service The network service handler (required, can not be null).
	 * @param channelID Network channel unique identification (required, not null).
	 * @param consumerConfig The kafka consumer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @param producerConfig The kafka producer configuration data (the consumer configuration or the producer configuration can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "service", "channelID" is null or empty, or the parameter "consumerConfig" and "producerConfig" is invalid.
	 */
	protected NetKafkaChannel(NetServiceHandler service, String channelID, NetKafkaConfigConsumer consumerConfig, NetKafkaConfigProducer producerConfig) throws IllegalArgumentException {
		super(service, channelID, true);
		// Create the consumer.
		if (consumerConfig == null || consumerConfig.props == null) {
			this.topics = null;
			this.pattern = null;
			this.pollTimeout = 100;
			this.consumer = null;
		} else {
			// Save parameters.
			this.topics = consumerConfig.topics;
			this.pattern = consumerConfig.pattern;
			this.pollTimeout = consumerConfig.pollTimeout >= 0 ? consumerConfig.pollTimeout : 100;
			// Check parameters.
			if ((topics != null && topics.length > 0) || !StringHelper.isEmpty(pattern)) {
				this.consumer = new KafkaConsumer<K, V>(consumerConfig.props);
			} else {
				this.consumer = null;
			}
		}
		// Create the producer.
		if (producerConfig != null && producerConfig.props != null) {
			this.producer = new KafkaProducer<K, V>(producerConfig.props);
		} else {
			this.producer = null;
		}
		// Check available.
		if (consumer == null || producer == null) {
			throw new IllegalArgumentException("The parameter configuration of consumerConfig or producerConfig is invalid!");
		}
	}

	@Override
	public final NetIO<?, ?> getToRemoteIO() {
		return producer == null ? null : new NetKafkaIO<>(this, null, producer);
	}

	@Override
	protected final boolean doOpen() throws Exception {
		if (consumer != null && consumerRunnable == null) {
			consumerRunnable = new ConsumerRunnable(this, consumer);
			Thread t = new Thread(consumerRunnable, "KafkaChannel-" + this.channelID);
			t.start();
		}
		return true;
	}

	@Override
	protected final boolean doReopen() throws Exception {
		return this.open();
	}

	@Override
	protected final boolean doClose() throws Exception {
		if (consumerRunnable != null) {
			consumerRunnable.setStop();
			consumerRunnable = null;
		}
		if (producer != null) {
			producer.close();
		}
		return true;
	}

	/**
	 * The consumer runnable object class.
	 * @author ardon
	 * @date 2021-08-09
	 */
	final class ConsumerRunnable implements Runnable {

		/**
		 * The network channel object.
		 */
		private final NetKafkaChannel<K, V> channel;
		/**
		 * The consumer object.
		 */
		private final KafkaConsumer<K, V> consumer;
		/**
		 * The stop status.
		 */
		private boolean stopped = false;

		/**
		 * Constructor for consumer messages.
		 * @param channel The network channel object.
		 * @param consumer The consumer object.
		 */
		ConsumerRunnable(NetKafkaChannel<K, V> channel, KafkaConsumer<K, V> consumer) {
			this.channel = channel;
			this.consumer = consumer;
		}

		/**
		 * Stop the consumer.
		 */
		final void setStop() {
			if (stopped) return;
			stopped = true;
			consumer.unsubscribe();
			consumer.wakeup();
		}

		@Override
		public final void run() {
			// Get network manager.
			final NetManager manager = channel.getService().getNetManager();
			try {
				// Subscribe the messages.
				if (topics != null && topics.length > 0) {
					consumer.subscribe(Arrays.asList(topics));
				} else {
					consumer.subscribe(Pattern.compile(pattern));
				}
				// Get the timeout duration.
				Duration duration = Duration.ofMillis(pollTimeout);
				// Poll the records.
				while (!stopped) {
					try {
						// Poll messages.
						ConsumerRecords<K, V> records = consumer.poll(duration);
						// Traverse message data
						for (ConsumerRecord<K, V> record : records) {
							// Notify message.
							manager.onMessage(new NetKafkaIO<>(channel, record, producer));
						}
					} catch (WakeupException e) {
						// Ignore exception if closing
						if (!stopped) throw e;
					}
				}
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			} finally {
				// Set to stopped.
				if (!stopped) stopped = true;
				// Close consumer.
				try {
					consumer.close();
				} catch (Exception e) {
					FrameworkNet.getLogger().error(e);
				}
				// Close channel.
				try {
					channel.close();
				} catch (Exception e) {
					FrameworkNet.getLogger().error(e);
				}
			}
		}

	}

}
