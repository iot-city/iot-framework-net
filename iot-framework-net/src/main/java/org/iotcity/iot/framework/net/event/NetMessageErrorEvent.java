package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.core.bus.BusEvent;

/**
 * The network message processing error event (this event can not be cancelled).
 * @author ardon
 * @date 2021-06-24
 */
public class NetMessageErrorEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for message processing error event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetMessageErrorEvent(Object source, NetMessageErrorEventData data) throws IllegalArgumentException {
		super(source, data, false);
	}

}
