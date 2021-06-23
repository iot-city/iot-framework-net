package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;

/**
 * The network channel event data for channel state changing.
 * @author ardon
 * @date 2021-06-21
 */
public class NetChannelEventData {

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
		this.channel = channel;
		this.state = state;
	}

	/**
	 * Gets the network channel object.
	 */
	public NetChannel getChannel() {
		return channel;
	}

	/**
	 * Gets the network channel state.
	 */
	public NetChannelState getState() {
		return state;
	}

}
