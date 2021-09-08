package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.session.NetSessionInfo;

/**
 * The actor header information for network transmission.
 * @author ardon
 * @date 2021-09-08
 */
public class NetActorHeader implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * An array of requested language keys (null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 */
	public String[] langs;
	/**
	 * An array of session information (null if there is no session information).
	 */
	public NetSessionInfo[] sessions;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor header.
	 */
	public NetActorHeader() {
	}

	/**
	 * Constructor for actor header.
	 * @param langs An array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @param sessions An array of session information (optional, set it to null if there is no session information).
	 */
	public NetActorHeader(String[] langs, NetSessionInfo[] sessions) {
		this.langs = langs;
		this.sessions = sessions;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{langs=");
		JavaHelper.getArrayPreview(langs, sb, false);
		sb.append(", sessions=");
		JavaHelper.getArrayPreview(sessions, sb, false);
		sb.append("}");
		return sb.toString();
	}

}
