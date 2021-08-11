package org.iotcity.iot.framework.net.kafka.config;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * The kafka consumer configuration data.
 * @author ardon
 * @date 2021-08-09
 */
public class NetKafkaConfigConsumer {

	/**
	 * Subscribe to the specified topics (if set it to null or empty elements, it will use the pattern regex string to subscribe to).
	 */
	public String[] topics;
	/**
	 * Subscribe to all topics matching specified pattern regex string to get dynamically assigned partitions.
	 */
	public String pattern;
	/**
	 * The time, in milliseconds, spent waiting in poll if data is not available in the buffer (100 by default).<br/>
	 * If 0, returns immediately with any records that are available currently in the buffer, else returns empty. Must not be negative.
	 */
	public long pollTimeout = 100;
	/**
	 * The consumer config map (required, can not be null).<br/>
	 * Valid configuration strings are documented here: <br/>
	 * http://kafka.apache.org/documentation.html#consumerconfigs
	 */
	public PropertiesMap<Object> props;

}
