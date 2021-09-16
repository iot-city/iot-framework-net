package org.iotcity.iot.framework.net;

import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;
import org.iotcity.iot.framework.net.io.NetDataRequest;
import org.iotcity.iot.framework.net.session.NetSessionInfo;

/**
 * A postman who sends network thread local data from one thread to another.
 * @author ardon
 * @date 2021-08-29
 */
public class NetThreadPostman implements ThreadLocalPostman {

	/**
	 * The session information array of current thread.
	 */
	private final NetSessionInfo[] sessionInfos;
	/**
	 * The network request data of current thread.
	 */
	private final NetDataRequest request;

	/**
	 * Constructor for network thread local postman.
	 */
	public NetThreadPostman() {
		this.sessionInfos = NetThreadLocal.getSessionInfos();
		this.request = NetThreadLocal.getCurrentRequest();
	}

	@Override
	public void storeToCurrentThread() {
		NetThreadLocal.setSessionInfos(sessionInfos);
		NetThreadLocal.setCurrentRequest(request);
	}

	@Override
	public void removeAll() {
		NetThreadLocal.removeAll();
	}

}
