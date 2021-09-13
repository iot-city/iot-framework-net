package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEventData;

/**
 * The kafka message processing error event (this event can not be cancelled).
 * @author ardon
 * @date 2021-06-28
 */
public final class NetKafkaMessageErrorEvent extends NetMessageErrorEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for kafka message processing error event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetKafkaMessageErrorEvent(Object source, NetMessageErrorEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
