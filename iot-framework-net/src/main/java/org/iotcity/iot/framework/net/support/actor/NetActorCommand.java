package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

/**
 * The actor command information for network transmission.
 * @author ardon
 * @date 2021-09-07
 */
public class NetActorCommand implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * The application ID (never null or empty).
	 */
	public String app;
	/**
	 * The application version (never null or empty).
	 */
	public String ver;
	/**
	 * The module ID in application (never null or empty).
	 */
	public String mid;
	/**
	 * Actor ID in module (never null or empty, equivalent to page ID).
	 */
	public String aid;
	/**
	 * The command ID in actor (never null or empty).
	 */
	public String cmd;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor command information.
	 */
	public NetActorCommand() {
	}

	/**
	 * Constructor for actor command information.
	 * @param app The application ID (required, can not be null or empty).
	 * @param ver The application version (required, can not be null or empty).
	 * @param mid The module ID in application (required, can not be null or empty).
	 * @param aid Actor ID in module (required, can not be null or empty, equivalent to page ID).
	 * @param cmd The command ID in actor (required, can not be null or empty).
	 */
	public NetActorCommand(String app, String ver, String mid, String aid, String cmd) {
		this.app = app;
		this.ver = ver;
		this.mid = mid;
		this.aid = aid;
		this.cmd = cmd;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{app=\"");
		sb.append(app);
		sb.append("\", ver=\"");
		sb.append(ver);
		sb.append("\", mid=\"");
		sb.append(mid);
		sb.append("\", aid=\"");
		sb.append(aid);
		sb.append("\", cmd=\"");
		sb.append(cmd);
		sb.append("\"}");
		return sb.toString();
	}

}
