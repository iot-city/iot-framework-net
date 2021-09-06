package org.iotcity.iot.framework.net.kafka.config;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * The kafka consumer configuration data.
 * @author ardon
 * @date 2021-08-09
 */
public class NetKafkaConfigConsumer {

	/**
	 * Subscribe to the specified topics to get dynamically assigned partitions (if set it to null or empty elements, it will use the patterns field to subscribe to).
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	public String[] topics;
	/**
	 * Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions (if set it to null or empty elements, it will use the partitions field to subscribe to).
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	public String pattern;
	/**
	 * Subscribe to the specified topics and partitions.
	 * 
	 * <pre>
	 * Only one of the three configurations can be used with the following priorities:
	 * 1. topics > 2. pattern > 3. partitions
	 * </pre>
	 */
	public NetKafkaConfigPartition[] partitions;
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
