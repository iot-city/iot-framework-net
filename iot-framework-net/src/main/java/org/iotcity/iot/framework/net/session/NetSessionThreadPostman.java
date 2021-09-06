package org.iotcity.iot.framework.net.session;

import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;

/**
 * A postman who sends thread local data of session from one thread to another.
 * @author ardon
 * @date 2021-08-29
 */
public class NetSessionThreadPostman implements ThreadLocalPostman {

	/**
	 * The session information array of current thread.
	 */
	private final NetSessionInfo[] sessionInfos;

	/**
	 * Constructor for thread local postman of session.
	 */
	public NetSessionThreadPostman() {
		this.sessionInfos = NetSessionThreadLocal.getSessionInfos();
	}

	@Override
	public void storeToCurrentThread() {
		NetSessionThreadLocal.setSessionInfos(sessionInfos);
	}

}
