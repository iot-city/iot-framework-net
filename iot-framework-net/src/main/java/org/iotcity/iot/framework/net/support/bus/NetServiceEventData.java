package org.iotcity.iot.framework.net.support.bus;

import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.channel.NetServiceState;

/**
 * The network service event data for service state changing.
 * @author ardon
 * @date 2021-06-22
 */
public class NetServiceEventData {

	/**
	 * The network service object.
	 */
	private final NetService service;
	/**
	 * The network service state.
	 */
	private final NetServiceState state;

	/**
	 * Constructor for network service event data.
	 * @param service The network service object.
	 * @param state The network service state.
	 */
	public NetServiceEventData(NetService service, NetServiceState state) {
		this.service = service;
		this.state = state;
	}

	/**
	 * Gets the network service object.
	 */
	public final NetService getService() {
		return service;
	}

	/**
	 * Gets the network service state.
	 */
	public final NetServiceState getState() {
		return state;
	}

}
