package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.net.io.NetInbound;

/**
 * The inbound object of inbound context.
 * @author ardon
 * @date 2021-06-23
 */
public final class NetInboundObject {

	/**
	 * Network inbound message processing object (not null).
	 */
	public final NetInbound<?, ?> inbound;
	/**
	 * Inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public final int priority;

	/**
	 * Constructor for inbound object of inbound context.
	 * @param inbound Network inbound message processing object (not null).
	 * @param priority Inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	public NetInboundObject(NetInbound<?, ?> inbound, int priority) {
		this.inbound = inbound;
		this.priority = priority;
	}

}
