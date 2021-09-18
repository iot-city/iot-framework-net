package org.iotcity.iot.framework.net.config;

/**
 * The configuration for serialization objects.
 * @author ardon
 * @date 2021-09-17
 */
public class NetConfigSerialization {

	/**
	 * The maximum capacity of free objects to store the serialization instance in object pool (8 by default).
	 */
	public int capacity = 8;
	/**
	 * The datetime format when serializing or deserializing, such as: "yyyy-MM-dd HH:mm:ss:SSS" (null by default).
	 */
	public String dateFormat;

	/**
	 * Constructor for serialization objects configuration.
	 */
	public NetConfigSerialization() {
	}

	/**
	 * Constructor for serialization objects configuration.
	 * @param capacity The maximum capacity of free objects to store the serialization instance in object pool (8 by default).
	 * @param dateFormat The datetime format when serializing or deserializing, such as: "yyyy-MM-dd HH:mm:ss:SSS" (null by default).
	 */
	public NetConfigSerialization(int capacity, String dateFormat) {
		this.capacity = capacity;
		this.dateFormat = dateFormat;
	}

}
