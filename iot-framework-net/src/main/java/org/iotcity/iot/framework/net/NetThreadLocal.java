package org.iotcity.iot.framework.net;

import org.iotcity.iot.framework.net.io.NetDataRequest;
import org.iotcity.iot.framework.net.session.NetSessionInfo;

/**
 * The network thread local variables.
 * @author ardon
 * @date 2021-08-29
 */
public final class NetThreadLocal {

	// --------------------------- Private fields ----------------------------

	/**
	 * The session information array of current thread.
	 */
	private static final ThreadLocal<NetSessionInfo[]> sessionInfos = new ThreadLocal<>();
	/**
	 * The network request data of current thread.
	 */
	private static final ThreadLocal<NetDataRequest> currentRequest = new ThreadLocal<>();

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Set session information array to thread local.
	 * @param infos The session information array.
	 */
	static final void setSessionInfos(NetSessionInfo[] infos) {
		sessionInfos.set(infos);
	}

	/**
	 * Set the network request data to current thread.
	 * @param request The network request data.
	 */
	static final void setCurrentRequest(NetDataRequest request) {
		currentRequest.set(request);
	}

	/**
	 * Remove all network thread local variables.
	 */
	static final void removeAll() {
		sessionInfos.remove();
		currentRequest.remove();
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the session information array of current thread (returns null value if it does not exist).
	 */
	public static final NetSessionInfo[] getSessionInfos() {
		return sessionInfos.get();
	}

	/**
	 * Gets the network request data of current thread.
	 */
	public static final NetDataRequest getCurrentRequest() {
		return currentRequest.get();
	}

}
