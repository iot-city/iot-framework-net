package org.iotcity.iot.framework.net.config;

/**
 * Network framework configuration data.
 * @author ardon
 * @date 2021-08-03
 */
public class NetConfig {

	/**
	 * The thread pool executor configure data of net manager.<br/>
	 * 1. This configuration will be used for message and request task processing.<br/>
	 * 2. When set it to null, the default pool options is used.
	 */
	public NetConfigPool pool = new NetConfigPool();
	/**
	 * The session group configure data array.<br/>
	 * Use this configuration to initialize session groups, and the group ID is consistent with the group ID defined in session manager.
	 */
	public NetConfigSession[] sessions;
	/**
	 * The service configure data array.
	 */
	public NetConfigService[] services;

}
