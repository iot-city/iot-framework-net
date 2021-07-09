package org.iotcity.iot.framework.net.channel;

/**
 * The network service configuration options data.
 * @author ardon
 * @date 2021-06-21
 */
public class NetServiceOptions {

	/**
	 * The service monitoring interval in milliseconds for channel status check (10000 ms by default).
	 */
	public long monitoringInterval = NetServiceHandler.CONST_MONITORING_INTERVAL;

}
