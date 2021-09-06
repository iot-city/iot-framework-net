package org.iotcity.iot.framework.net.session;

/**
 * The session thread local variables.
 * @author ardon
 * @date 2021-08-29
 */
public final class NetSessionThreadLocal {

	// --------------------------- Private fields ----------------------------

	/**
	 * The session information array of current thread.
	 */
	private static final ThreadLocal<NetSessionInfo[]> sessionInfos = new ThreadLocal<>();

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Set session information array to thread local.
	 * @param infos The session information array.
	 */
	static final void setSessionInfos(NetSessionInfo[] infos) {
		sessionInfos.set(infos);
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the session information array of current thread (returns null value if it does not exist).
	 */
	public static final NetSessionInfo[] getSessionInfos() {
		return sessionInfos.get();
	}

}
