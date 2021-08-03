package org.iotcity.iot.framework.net.config;

/**
 * The session group configure data.
 * @author ardon
 * @date 2021-08-03
 */
public class NetConfigSession {

	/**
	 * The session group ID (required, can not be null or empty).
	 */
	public String groupID;
	/**
	 * Indicates whether the current session group is enabled (true by default).
	 */
	public boolean enabled = true;
	/**
	 * The maximum number of sessions in current group (0 by default, set to 0 if you do not need to limit the number of sessions).
	 */
	public int maximums = 0;
	/**
	 * If there is no data interaction within the timeout period, the session will be destroyed (in milliseconds, 60000 ms by default, set to 0 if timeout is not required).
	 */
	public long timeout = 60000;
	/**
	 * A proxy object class that handles the session data.
	 */
	public Class<?> proxy;

}
