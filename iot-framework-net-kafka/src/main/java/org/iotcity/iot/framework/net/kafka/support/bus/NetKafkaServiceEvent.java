package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetServiceEvent;
import org.iotcity.iot.framework.net.support.bus.NetServiceEventData;

/**
 * The kafka service event for service state changing.
 * @author ardon
 * @date 2021-06-22
 */
public final class NetKafkaServiceEvent extends NetServiceEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for kafka service event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public NetKafkaServiceEvent(Object source, NetServiceEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
