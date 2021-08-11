package org.iotcity.iot.framework.net.kafka.config;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * The kafka producer configuration data.
 * @author ardon
 * @date 2021-08-09
 */
public class NetKafkaConfigProducer {

	/**
	 * The producer config map (required, can not be null).<br/>
	 * Valid configuration strings are documented here: <br/>
	 * http://kafka.apache.org/documentation.html#producerconfigs
	 */
	public PropertiesMap<Object> props;

}
