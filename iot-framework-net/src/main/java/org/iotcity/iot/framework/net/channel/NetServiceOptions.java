package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.net.config.NetConfigInbound;
import org.iotcity.iot.framework.net.config.NetConfigOutbound;

/**
 * The network service configuration options data.
 * @author ardon
 * @date 2021-08-07
 */
public class NetServiceOptions {

	/**
	 * The network service inbound configuration data array (null by default).
	 */
	public NetConfigInbound[] inbounds;
	/**
	 * The network service outbound configuration data array (null by default).
	 */
	public NetConfigOutbound[] outbounds;
	/**
	 * The service monitoring interval in milliseconds for channel status check (10000 ms by default).
	 */
	public long monitoringInterval = NetServiceHandler.CONST_MONITORING_INTERVAL;

}
