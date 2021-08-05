package org.iotcity.iot.framework.net.config;

/**
 * The network outbound configuration data.
 * @author ardon
 * @date 2021-08-05
 */
public class NetConfigOutbound {

	/**
	 * The service outbound instance class (required, can not be null).
	 */
	public Class<?> instance;
	/**
	 * The service outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public int priority;

}
