package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.net.session.NetSession;
import org.iotcity.iot.framework.net.session.NetSessionState;

/**
 * The network session event data for session state changing.
 * @author ardon
 * @date 2021-06-22
 */
public class NetSessionEventData {

	/**
	 * The network session object.
	 */
	private final NetSession session;
	/**
	 * The network session state.
	 */
	private final NetSessionState state;

	/**
	 * Constructor for network session event data.
	 * @param session The network session object.
	 * @param state The network session state.
	 */
	public NetSessionEventData(NetSession session, NetSessionState state) {
		this.session = session;
		this.state = state;
	}

	/**
	 * Gets the network session object.
	 */
	public final NetSession getSession() {
		return session;
	}

	/**
	 * Gets the network session state.
	 */
	public final NetSessionState getState() {
		return state;
	}

}
