package org.iotcity.iot.framework.net.session;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.net.config.NetConfigSession;

/**
 * @author ardon
 * @date 2021-07-29
 */
public class NetSessionManager implements Configurable<NetConfigSession[]> {

	/**
	 * Constructor for
	 */
	public NetSessionManager() {
	}

	@Override
	public boolean config(NetConfigSession[] data, boolean reset) {
		return false;
	}

}
