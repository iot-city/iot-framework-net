package org.iotcity.iot.framework.net.support.bus;

import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.net.channel.NetServiceState;

/**
 * The network service event for service state changing.
 * @author ardon
 * @date 2021-06-21
 */
public class NetServiceEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for network service event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetServiceEvent(Object source, NetServiceEventData data) throws IllegalArgumentException {
		super(source, data, data.getState() == NetServiceState.STARTING || data.getState() == NetServiceState.STOPPING);
	}

}
