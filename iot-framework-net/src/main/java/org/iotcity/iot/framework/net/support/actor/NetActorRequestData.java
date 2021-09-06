package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.net.session.NetSessionInfo;

/**
 * Actor request data for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorRequestData implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * An array of session information for request data.
	 */
	public NetSessionInfo[] sessions;
	/**
	 * An array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 */
	public String[] langs;
	/**
	 * The application ID (not null or empty).
	 */
	public String app;
	/**
	 * The application version (not null or empty).
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
	 * An array of parameters that be used to invoke the method.
	 */
	public Serializable[] params;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor request data.
	 */
	public NetActorRequestData() {
	}

	/**
	 * Constructor for actor request data.
	 * @param sessions An array of session information for request data.
	 * @param langs An array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @param app The application ID (can not be null or empty).
	 * @param ver The application version (can not be null or empty).
	 * @param mid The module ID in application (can not be null or empty).
	 * @param aid Actor ID in module (can not be null or empty, equivalent to page ID).
	 * @param cmd The command ID in actor (can not be null or empty).
	 * @param params An array of parameters that be used to invoke the method.
	 */
	public NetActorRequestData(NetSessionInfo[] sessions, String[] langs, String app, String ver, String mid, String aid, String cmd, Serializable[] params) {
		this.sessions = sessions;
		this.langs = langs;
		this.app = app;
		this.ver = ver;
		this.mid = mid;
		this.aid = aid;
		this.cmd = cmd;
		this.params = params;
	}

}
