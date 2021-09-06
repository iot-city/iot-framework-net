package org.iotcity.iot.framework.net.support.bus;

import org.iotcity.iot.framework.core.bus.BusEvent;

/**
 * The network session event for session state changing.
 * @author ardon
 * @date 2021-06-21
 */
public class NetSessionEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for network session event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetSessionEvent(Object source, NetSessionEventData data) throws IllegalArgumentException {
		super(source, data, true);
	}

}
