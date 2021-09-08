package org.iotcity.iot.framework.net.kafka.config;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * The producer callback consumer configuration data.
 * @author ardon
 * @date 2021-09-08
 */
public class NetKafkaConfigCallback {

	/**
	 * Subscribe to the topic name to get callback data (required, can not be null or empty).
	 */
	public String topic;
	/**
	 * Subscribe to the partition of the topic name (required).
	 */
	public int partition;
	/**
	 * The timeout value in milliseconds, spent waiting in poll if data is not available in the buffer (200 by default).<br/>
	 * If 0, returns immediately with any records that are available currently in the buffer, else returns empty. Must not be negative.
	 */
	public long pollTimeout = 200;
	/**
	 * The maximum time in milliseconds to wait for consumer to close gracefully (30000 by default).<br/>
	 * Specifying a timeout of zero means do not wait for pending requests to complete.
	 */
	public long closeTimeout = 30000;
	/**
	 * The consumer config map (required, can not be null).<br/>
	 * Valid configuration strings are documented here: <br/>
	 * http://kafka.apache.org/documentation.html#consumerconfigs
	 */
	public PropertiesMap<Object> props;

}
