package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.net.io.NetOutbound;

/**
 * The outbound object of outbound context.
 * @author ardon
 * @date 2021-06-23
 */
public final class NetOutboundObject {

	/**
	 * Network outbound message processing object (not null).
	 */
	public final NetOutbound<?, ?> outbound;
	/**
	 * Outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public final int priority;

	/**
	 * Constructor for outbound object of outbound context.
	 * @param outbound Network outbound message processing object (not null).
	 * @param priority Outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public NetOutboundObject(NetOutbound<?, ?> outbound, int priority) {
		this.outbound = outbound;
		this.priority = priority;
	}

}
