package org.iotcity.iot.framework.net.kafka.config;

/**
 * The kafka topic partition configuration data.
 * @author ardon
 * @date 2021-08-13
 */
public class NetKafkaConfigPartition {

	/**
	 * The topic name.
	 */
	public String topic;
	/**
	 * The kafka partition id array of current topic (e.g. partitions=0, 1, 3, if set it to null or an array with empty value, it will subscribe to all partitions for current topic).
	 */
	public int[] partitions;

}
