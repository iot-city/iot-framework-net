package org.iotcity.iot.framework.net.config;

import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;

/**
 * The network service configuration data.
 * @author ardon
 * @date 2021-08-03
 */
public class NetConfigService {

	/**
	 * The service unique identification (required, can not be null or empty).
	 */
	public String serviceID;
	/**
	 * Indicates whether the current service is enabled (true by default).
	 */
	public boolean enabled = true;
	/**
	 * The service instance class (required, can not be null).
	 */
	public Class<?> instance;
	/**
	 * The network service inbound configuration data array.
	 */
	public NetConfigInbound[] inbounds;
	/**
	 * The network service outbound configuration data array.
	 */
	public NetConfigOutbound[] outbounds;
	/**
	 * The service configuration file.
	 */
	public PropertiesConfigFile config;
	/**
	 * Indicates whether the service starts automatically after configuration (true by default).
	 */
	public boolean autoStart = true;
	/**
	 * The delayed start time in milliseconds for automatically starting the service (0 ms by default).
	 */
	public long autoStartDelay = 0;
	/**
	 * The service monitoring interval in milliseconds for channel status check (10000 ms by default).
	 */
	public long monitoringInterval = NetServiceHandler.CONST_MONITORING_INTERVAL;

}
