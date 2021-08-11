package org.iotcity.iot.framework.net.kafka.config;

import org.iotcity.iot.framework.net.channel.NetChannelOptions;

/**
 * The channel configuration data for kafka.
 * @author ardon
 * @date 2021-08-09
 */
public class NetKafkaConfigChannel {

	/**
	 * The channel unique identification (required, can not be null or empty).
	 */
	public String channelID;
	/**
	 * Indicates whether the current channel is enabled (true by default).
	 */
	public boolean enabled = true;
	/**
	 * The channel instance class (required, can not be null).<br/>
	 * The variable channel classes in the framework:<br/>
	 * 
	 * <pre>
	 * org.iotcity.iot.framework.net.kafka.channels.NetKafkaStringChannel<br/>
	 * org.iotcity.iot.framework.net.kafka.channels.NetKafkaByteChannel<br/>
	 * org.iotcity.iot.framework.net.kafka.channels.NetKafkaStringByteChannel<br/>
	 * </pre>
	 */
	public Class<?> instance;
	/**
	 * The network channel configuration options data (optional).
	 */
	public NetChannelOptions options;
	/**
	 * The kafka consumer configuration data (the consumer configuration or the producer configuration can not be null).
	 */
	public NetKafkaConfigConsumer consumer;
	/**
	 * The kafka producer configuration data (the consumer configuration or the producer configuration can not be null).
	 */
	public NetKafkaConfigProducer producer;

}
