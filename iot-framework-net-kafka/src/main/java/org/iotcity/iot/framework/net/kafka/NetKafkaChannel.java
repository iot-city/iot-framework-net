package org.iotcity.iot.framework.net.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannelHandler;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigPartition;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;

/**
 * The kafka channel for message consumer and producer.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @author ardon
 * @date 2021-06-16
 */
public class NetKafkaChannel<K, V> extends NetChannelHandler {

	/**
	 * Subscribe to the specified topics (if set it to null or empty elements, it will use the patterns field to subscribe to).
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	private final String[] topics;
	/**
	 * Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions (if set it to null or empty elements, it will use the partitions field to subscribe to).
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	private final String pattern;
	/**
	 * Subscribe to the specified topics and partitions.
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	private final NetKafkaConfigPartition[] partitions;
	/**
	 * The time, in milliseconds, spent waiting in poll if data is not available in the buffer (200 by default).<br/>
	 * If 0, returns immediately with any records that are available currently in the buffer, else returns empty. Must not be negative.
	 */
	private final long pollTimeout;
	/**
	 * The maximum time in milliseconds to wait for consumer to close gracefully (30000 by default).<br/>
	 * Specifying a timeout of zero means do not wait for pending requests to complete.
	 */
	private final long closeTimeout;
	/**
	 * Indicates whether the consumer offsets are submitted automatically.
	 */
	private final boolean autoCommitOffset;
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
			// Init parameters.
			this.topics = null;
			this.pattern = null;
			this.partitions = null;
			this.pollTimeout = 200;
			this.closeTimeout = 30000;
			this.consumer = null;
			this.autoCommitOffset = true;
		} else {
			// Save parameters.
			this.topics = consumerConfig.topics;
			this.pattern = consumerConfig.pattern;
			this.partitions = consumerConfig.partitions;
			this.pollTimeout = consumerConfig.pollTimeout >= 0 ? consumerConfig.pollTimeout : 200;
			this.closeTimeout = consumerConfig.closeTimeout >= 0 ? consumerConfig.closeTimeout : 30000;
			this.autoCommitOffset = ConvertHelper.toBoolean(consumerConfig.props.get("auto.commit.offset"), true);
			// Check parameters.
			if ((topics != null && topics.length > 0) || !StringHelper.isEmpty(pattern) || (partitions != null && partitions.length > 0)) {
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
		return producer == null ? null : new NetKafkaIO<>(this, consumer, null, producer);
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
			try {
				producer.close();
			} catch (Exception e) {
				logger.error(e);
			}
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
		 * The consumer runnable lock.
		 */
		private final Object consumerLock = new Object();
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
			// Lock for consumer.
			synchronized (consumerLock) {
				// Wake up the consumer.
				consumer.wakeup();
				try {
					// Wait for finishing.
					consumerLock.wait(closeTimeout * 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public final void run() {
			// Check for stopped.
			if (stopped) return;
			// Get network manager.
			final NetManager manager = channel.getService().getNetManager();
			try {

				// Check subscribe configuration.
				if (topics != null && topics.length > 0) {
					// Subscribe by topic names.
					if (autoCommitOffset) {
						consumer.subscribe(Arrays.asList(topics));
					} else {
						consumer.subscribe(Arrays.asList(topics), new HandleConsumerRebalance(consumer));
					}
				} else if (!StringHelper.isEmpty(pattern)) {
					// Subscribe by pattern regex.
					if (autoCommitOffset) {
						consumer.subscribe(Pattern.compile(pattern));
					} else {
						consumer.subscribe(Pattern.compile(pattern), new HandleConsumerRebalance(consumer));
					}
				} else {
					// Subscribe by partitions.
					subscribeByPartitions();
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
							manager.onMessage(new NetKafkaIO<>(channel, consumer, record, producer));
						}
						// Commit offset by async mode if "auto.commit.offset=false".
						if (!autoCommitOffset) consumer.commitAsync();
					} catch (WakeupException e) {
						// Ignore exception if closing
						if (!stopped) throw e;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				// Set to stopped.
				if (!stopped) stopped = true;
				try {
					// Commit offset by sync mode again.
					if (!autoCommitOffset) {
						try {
							consumer.commitSync();
						} catch (Exception e) {
							logger.error(e);
						}
					}
				} finally {
					// Close consumer.
					try {
						consumer.close(Duration.ofMillis((closeTimeout)));
					} catch (Exception e) {
						logger.error(e);
					}
					// Run unsubscribe after close to ensure the offsets committing.
					try {
						consumer.unsubscribe();
					} catch (Exception e) {
						logger.error(e);
					}
				}
				// Unlock the waiting.
				synchronized (consumerLock) {
					try {
						consumerLock.notify();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Close channel.
				try {
					channel.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}

		/**
		 * Subscribe to the specified topics and partitions.
		 */
		private final void subscribeByPartitions() {
			// Defined partitions list.
			List<TopicPartition> list = new ArrayList<>();
			// Traverse partitions configured.
			for (NetKafkaConfigPartition cp : partitions) {
				String topic = cp.topic;
				int[] pids = cp.partitions;
				// Determine whether to subscribe to all partitions.
				if (pids == null || pids.length == 0) {
					// Subscribe to all partitions.
					List<PartitionInfo> partitions = consumer.partitionsFor(topic);
					for (PartitionInfo partition : partitions) {
						list.add(new TopicPartition(partition.topic(), partition.partition()));
					}
				} else {
					// Subscribe to specified partitions.
					for (int pid : pids) {
						list.add(new TopicPartition(topic, pid));
					}
				}
			}
			// Subscribe partitions.
			consumer.assign(list);
		}

	}

	/**
	 * The consumer submits the offset before exiting and re-balancing the partition.
	 * @author ardon
	 * @date 2021-08-13
	 */
	final class HandleConsumerRebalance implements ConsumerRebalanceListener {

		/**
		 * The consumer object.
		 */
		private final KafkaConsumer<K, V> consumer;

		/**
		 * Constructor for consumer re-balance submitting object.
		 * @param consumer The consumer object.
		 */
		HandleConsumerRebalance(KafkaConsumer<K, V> consumer) {
			this.consumer = consumer;
		}

		@Override
		public final void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		}

		@Override
		public final void onPartitionsRevoked(Collection<TopicPartition> partitions) {
			try {
				consumer.commitSync();
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

}
