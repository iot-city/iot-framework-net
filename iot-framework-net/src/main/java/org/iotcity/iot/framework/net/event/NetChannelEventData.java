package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.channel.NetService;

/**
 * The network channel event data for channel state changing.
 * @author ardon
 * @date 2021-06-21
 */
public class NetChannelEventData {

	/**
	 * The network service object.
	 */
	private final NetService service;
	/**
	 * The network channel object.
	 */
	private final NetChannel channel;
	/**
	 * The network channel state.
	 */
	private final NetChannelState state;

	/**
	 * Constructor for network channel event data.
	 * @param channel The network channel object.
	 * @param state The network channel state.
	 */
	public NetChannelEventData(NetChannel channel, NetChannelState state) {
		this.service = channel.getService();
		this.channel = channel;
		this.state = state;
	}

	/**
	 * Gets the network service object.
	 */
	public final NetService getService() {
		return service;
	}

	/**
	 * Gets the network channel object.
	 */
	public final NetChannel getChannel() {
		return channel;
	}

	/**
	 * Gets the network channel state.
	 */
	public final NetChannelState getState() {
		return state;
	}

}
