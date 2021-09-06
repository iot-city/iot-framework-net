package org.iotcity.iot.framework.net.config;

/**
 * Network framework configuration data.
 * @author ardon
 * @date 2021-08-03
 */
public class NetConfig {

	/**
	 * The unique identification of the current server, which is used for distributed access.<br/>
	 * If it is not set, the current server IP address or MAC address will be automatically used as the default value.
	 */
	public String serverID;
	/**
	 * The thread pool executor configure data of net manager.<br/>
	 * 1. This configuration will be used for message and request task processing.<br/>
	 * 2. When without this option, the global task handler instance in framework core is used.
	 */
	public NetConfigPool pool;
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
