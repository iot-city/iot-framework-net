package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.net.channel.NetChannelState;

/**
 * The network channel event for channel state changing.
 * @author ardon
 * @date 2021-06-21
 */
public class NetChannelEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for network channel event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetChannelEvent(Object source, NetChannelEventData data) throws IllegalArgumentException {
		super(source, data, data.getState() == NetChannelState.OPENING || data.getState() == NetChannelState.CLOSING);
	}

}
