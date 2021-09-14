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
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannelHandler;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigCallback;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigPartition;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSender;

/**
 * The kafka channel for message consumer and producer.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetKafkaChannel<K, V> extends NetChannelHandler {

	/**
	 * The kafka producer configuration.
	 */
	private final PropertiesMap<Object> producerConfig;
	/**
	 * The producer callback partition information.
	 */
	private final NetKafkaTopicPartition callback;
	/**
	 * The configuration for callback consumer.
	 */
	private final ConsumerConfig cllbackConfig;
	/**
	 * The configuration for request consumer.
	 */
	private final ConsumerConfig consumerConfig;

	/**
	 * The kafka message sender object.
	 */
	private NetKafkaSender<K, V> sender;
	/**
	 * The consumer runnable object for callback.
	 */
	private ConsumerRunnable<K, V> callbackRunnable;
	/**
	 * The consumer runnable object for message requests.
	 */
	private ConsumerRunnable<K, V> consumerRunnable;

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

		// Check producer configuration.
		if (producerConfig != null && producerConfig.props != null) {

			// Set producer configuration.
			this.producerConfig = producerConfig.props;

			// Initialize callback information.
			NetKafkaConfigCallback cbk = producerConfig.callback;
			if (cbk == null || StringHelper.isEmpty(cbk.topic) || cbk.partition < 0 || cbk.props == null) {
				this.callback = null;
				this.cllbackConfig = null;
			} else {
				this.callback = new NetKafkaTopicPartition(cbk.topic, cbk.partition);
				// Create configuration.
				NetKafkaConfigPartition partition = new NetKafkaConfigPartition();
				partition.topic = cbk.topic;
				partition.partitions = new int[] {
					cbk.partition
				};
				// Create callback configuration.
				this.cllbackConfig = new ConsumerConfig(null, null, new NetKafkaConfigPartition[] {
					partition
				}, cbk.pollTimeout, cbk.closeTimeout, cbk.props);
			}

		} else {
			this.producerConfig = null;
			this.callback = null;
			this.cllbackConfig = null;
		}

		// Check consumer configuration.
		if (consumerConfig == null || consumerConfig.props == null) {
			this.consumerConfig = null;
		} else {
			// Get configuration.
			NetKafkaConfigConsumer cc = consumerConfig;
			// Check parameters.
			if ((cc.topics != null && cc.topics.length > 0) || !StringHelper.isEmpty(cc.pattern) || (cc.partitions != null && cc.partitions.length > 0)) {
				// Create configuration.
				this.consumerConfig = new ConsumerConfig(cc.topics, cc.pattern, cc.partitions, cc.pollTimeout, cc.closeTimeout, cc.props);
			} else {
				this.consumerConfig = null;
			}
		}

		// Check available.
		if (this.consumerConfig == null && this.producerConfig == null) {
			throw new IllegalArgumentException("The parameter configuration of consumerConfig or producerConfig is invalid!");
		}

	}

	/**
	 * Gets the kafka message sender object (returns null if there is no sender).
	 */
	protected NetKafkaSender<K, V> getSender() {
		return this.sender;
	}

	@Override
	protected final boolean doOpen(long openingID) throws Exception {
		// Create a producer sender object.
		if (producerConfig != null && sender == null) {
			// Create sender.
			sender = new NetKafkaSender<>(this, new KafkaProducer<K, V>(producerConfig), callback);
		}
		// Create and run message consumer.
		if (consumerConfig != null && (consumerRunnable == null || consumerRunnable.isStopped())) {
			consumerRunnable = new ConsumerRunnable<>(this, consumerConfig, openingID);
			Thread t = new Thread(consumerRunnable, "KafkaChannel-Message-" + this.channelID);
			t.start();
		}
		// Create and run callback consumer.
		if (cllbackConfig != null && (callbackRunnable == null || callbackRunnable.isStopped())) {
			callbackRunnable = new ConsumerRunnable<>(this, cllbackConfig, openingID);
			Thread t = new Thread(callbackRunnable, "KafkaChannel-Callback-" + this.channelID);
			t.start();
		}
		return true;
	}

	@Override
	protected final boolean doReopen() throws Exception {
		return this.open() > 0;
	}

	@Override
	protected final boolean doClose() throws Exception {
		// Stop message consumer.
		if (consumerRunnable != null) {
			consumerRunnable.setStop();
			consumerRunnable = null;
		}
		// Stop callback consumer.
		if (callbackRunnable != null) {
			callbackRunnable.setStop();
			callbackRunnable = null;
		}
		// Stop message producer.
		if (sender != null) {
			try {
				sender.getProducer().close();
			} catch (Exception e) {
				logger.error(e);
			}
			sender = null;
		}
		return true;
	}

	/**
	 * Gets the from remote I/O object (returns not null).
	 * @param consumer A client that consumes records from a Kafka cluster.
	 * @param record A key/value pair to be received from Kafka.
	 * @return The from remote I/O object.
	 */
	protected abstract NetIO<?, ?> getFromRemoteIO(KafkaConsumer<K, V> consumer, ConsumerRecord<K, V> record);

	/**
	 * The kafka consumer configuration.
	 * @author ardon
	 * @date 2021-09-08
	 */
	final static class ConsumerConfig {

		/**
		 * Subscribe to the specified topics (if set it to null or empty elements, it will use the patterns field to subscribe to).
		 */
		final String[] topics;
		/**
		 * Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions (if set it to null or empty elements, it will use the partitions field to subscribe to).
		 */
		final String pattern;
		/**
		 * Subscribe to the specified topics and partitions.
		 */
		final NetKafkaConfigPartition[] partitions;
		/**
		 * The time, in milliseconds, spent waiting in poll if data is not available in the buffer (200 by default).
		 */
		final long pollTimeout;
		/**
		 * The maximum time in milliseconds to wait for consumer to close gracefully (30000 by default).
		 */
		final long closeTimeout;
		/**
		 * Indicates whether the consumer offsets are submitted automatically.
		 */
		final boolean autoCommitOffset;
		/**
		 * The consumer config map (required, can not be null).
		 */
		final PropertiesMap<Object> props;

		/**
		 * Constructor for consumer configuration.
		 * @param topics Subscribe to the specified topics.
		 * @param pattern Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions.
		 * @param partitions Subscribe to the specified topics and partitions.
		 * @param pollTimeout The time, in milliseconds, spent waiting in poll if data is not available in the buffer (200 by default).
		 * @param closeTimeout The maximum time in milliseconds to wait for consumer to close gracefully (30000 by default).
		 * @param props The consumer config map (required, can not be null).
		 */
		ConsumerConfig(String[] topics, String pattern, NetKafkaConfigPartition[] partitions, long pollTimeout, long closeTimeout, PropertiesMap<Object> props) {
			// Save parameters.
			this.topics = topics;
			this.pattern = pattern;
			this.partitions = partitions;
			this.pollTimeout = pollTimeout >= 0 ? pollTimeout : 200;
			this.closeTimeout = closeTimeout >= 0 ? closeTimeout : 30000;
			this.autoCommitOffset = ConvertHelper.toBoolean(props.get("enable.auto.commit"), false);
			this.props = props;
		}

	}

	/**
	 * The consumer runnable object class.
	 * @author ardon
	 * @date 2021-08-09
	 */
	final static class ConsumerRunnable<K, V> implements Runnable {

		/**
		 * The network channel object.
		 */
		private final NetKafkaChannel<K, V> channel;
		/**
		 * The kafka consumer configuration.
		 */
		private final ConsumerConfig config;
		/**
		 * A client that consumes records from a kafka cluster.
		 */
		private final KafkaConsumer<K, V> consumer;
		/**
		 * The consumer runnable lock.
		 */
		private final Object consumerLock = new Object();
		/**
		 * Unique sequence number for channel opened.
		 */
		private final long openingID;
		/**
		 * The stop status.
		 */
		private boolean stopped = false;

		/**
		 * Constructor for consumer messages.
		 * @param channel The network channel object.
		 * @param config The kafka consumer configuration.
		 * @param openingID Unique sequence number for channel opened.
		 */
		ConsumerRunnable(NetKafkaChannel<K, V> channel, ConsumerConfig config, long openingID) {
			this.channel = channel;
			this.config = config;
			this.openingID = openingID;
			this.consumer = new KafkaConsumer<K, V>(config.props);
		}

		/**
		 * Gets the kafka consumer (never null).
		 */
		final KafkaConsumer<K, V> getConsumer() {
			return consumer;
		}

		/**
		 * Determine whether it has stopped.
		 */
		final boolean isStopped() {
			return stopped;
		}

		/**
		 * Stop the consumer.
		 */
		final void setStop() {
			if (stopped) return;
			// Lock for consumer.
			synchronized (consumerLock) {
				if (stopped) return;
				stopped = true;
				// Wake up the consumer.
				consumer.wakeup();
				try {
					// Wait for finishing.
					consumerLock.wait(config.closeTimeout * 2);
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
			// Get the channel logger.
			final Logger logger = channel.getLogger();
			try {

				// Subscribe to the specified topics and partitions.
				subscribeMessage(logger);

				// Get the timeout duration.
				Duration duration = Duration.ofMillis(config.pollTimeout);
				// Poll the records.
				while (!stopped) {
					try {
						// Poll messages.
						ConsumerRecords<K, V> records = consumer.poll(duration);
						// Traverse message data
						for (ConsumerRecord<K, V> record : records) {
							// Notify message.
							manager.onMessage(channel.getFromRemoteIO(consumer, record));
						}
						// Commit offset by async mode if "enable.auto.commit=false".
						if (!config.autoCommitOffset) consumer.commitAsync();
					} catch (WakeupException e) {
						// Ignore exception if closing
						if (!stopped) throw e;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {

					// Commit offset by sync mode again.
					if (!config.autoCommitOffset) {
						try {
							consumer.commitSync();
						} catch (Exception e) {
							logger.error(e);
						}
					}

				} finally {

					// Run unsubscribe after close to ensure the offsets committing.
					try {
						consumer.unsubscribe();
					} catch (Exception e) {
						logger.error(e);
					}
					// Close consumer.
					try {
						consumer.close(Duration.ofMillis(config.closeTimeout));
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
					// Check stopped status.
					if (!stopped) {
						// Set to stopped.
						stopped = true;
						// Close channel.
						try {
							channel.closeFor(openingID);
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

			}
		}

		/**
		 * Subscribe to the specified topics and partitions.
		 */
		private final void subscribeMessage(Logger logger) {

			// Check subscribe configuration.
			if (config.topics != null && config.topics.length > 0) {

				// Subscribe by topic names.
				if (config.autoCommitOffset) {
					consumer.subscribe(Arrays.asList(config.topics));
				} else {
					consumer.subscribe(Arrays.asList(config.topics), new HandleConsumerRebalance<K, V>(consumer, logger));
				}

			} else if (!StringHelper.isEmpty(config.pattern)) {

				// Subscribe by pattern regex.
				if (config.autoCommitOffset) {
					consumer.subscribe(Pattern.compile(config.pattern));
				} else {
					consumer.subscribe(Pattern.compile(config.pattern), new HandleConsumerRebalance<K, V>(consumer, logger));
				}

			} else {

				// Subscribe to the specified topics and partitions.
				List<TopicPartition> list = new ArrayList<>();
				// Traverse partitions configured.
				for (NetKafkaConfigPartition cp : config.partitions) {
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

	}

	/**
	 * The consumer submits the offset before exiting and re-balancing the partition.
	 * @author ardon
	 * @date 2021-08-13
	 */
	final static class HandleConsumerRebalance<K, V> implements ConsumerRebalanceListener {

		/**
		 * The consumer object.
		 */
		private final KafkaConsumer<K, V> consumer;
		/**
		 * The logger object.
		 */
		private final Logger logger;

		/**
		 * Constructor for consumer re-balance submitting object.
		 * @param consumer The consumer object.
		 * @param logger The logger object.
		 */
		HandleConsumerRebalance(KafkaConsumer<K, V> consumer, Logger logger) {
			this.consumer = consumer;
			this.logger = logger;
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
