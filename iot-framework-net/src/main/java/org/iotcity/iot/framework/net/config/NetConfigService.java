package org.iotcity.iot.framework.net.config;

import org.iotcity.iot.framework.net.channel.NetServiceOptions;
import org.iotcity.iot.framework.net.support.actor.NetActorClassFactory;

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
	 * The service instance class (required, can not be null).
	 */
	public Class<?> instance;
	/**
	 * Class factory for inbound or outbound data serialization, e.g. a class implements {@link NetActorClassFactory } (null by default).
	 */
	public Class<?> classFactory;
	/**
	 * Indicates whether the service starts automatically after configuration (true by default).
	 */
	public boolean autoStart = true;
	/**
	 * The delayed start time in milliseconds for automatically starting the service (0 ms by default).
	 */
	public long autoStartDelay = 0;
	/**
	 * The service configuration options (null by default).
	 */
	public NetServiceOptions options;

}
