package org.iotcity.iot.framework.net.config;

/**
 * The network inbound configuration data.
 * @author ardon
 * @date 2021-08-05
 */
public class NetConfigInbound {

	/**
	 * The service inbound instance class (required, can not be null).
	 */
	public Class<?> instance;
	/**
	 * The service inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public int priority;

}
