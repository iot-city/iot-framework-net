package org.iotcity.iot.framework.net;

import org.iotcity.iot.framework.net.io.NetMessager;

/**
 * @author ardon
 * @date 2021-05-16
 */
public class NetManager {

	/**
	 * The network message processing object used to process inbound data.
	 */
	private final NetMessager messager = new NetMessager();

	/**
	 * Constructor for
	 */
	public NetManager() {
	}

	/**
	 * Gets the network message data processing object (returns not null).
	 */
	public NetMessager getMessager() {
		return messager;
	}

}
