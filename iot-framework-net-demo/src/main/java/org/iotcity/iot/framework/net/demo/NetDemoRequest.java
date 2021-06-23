package org.iotcity.iot.framework.net.demo;

import java.io.Serializable;

import org.iotcity.iot.framework.net.io.NetDataRequest;

/**
 * @author ardon
 * @date 2021-06-15
 */
public class NetDemoRequest extends NetDataRequest {

	/**
	 * The user token for session management.
	 */
	public String token;
	/**
	 * Array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 */
	public String[] langs;
	/**
	 * The application ID (not null or empty).
	 */
	public String app;
	/**
	 * The application version (optional, when it is null, the default value is "1.0.0").
	 */
	public String ver;
	/**
	 * The module ID in application (not null or empty).
	 */
	public String mid;
	/**
	 * Actor ID in module (not null or empty, equivalent to page ID).
	 */
	public String aid;
	/**
	 * The command ID in actor (not null or empty).
	 */
	public String cmd;
	/**
	 * The array of parameters that be used to invoke the method.
	 */
	public Serializable[] params;

}
