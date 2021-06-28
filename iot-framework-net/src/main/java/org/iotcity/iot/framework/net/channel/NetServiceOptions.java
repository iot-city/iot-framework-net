package org.iotcity.iot.framework.net.channel;

/**
 * The network service global configuration option data.
 * @author ardon
 * @date 2021-06-21
 */
public class NetServiceOptions {

	/**
	 * The global configuration timeout value in milliseconds that waiting for a response data callback (the default timeout value is 120000 ms).
	 */
	public long defaultCallbackTimeout = 120000;

}
