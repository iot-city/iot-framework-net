package org.iotcity.iot.framework.net.kafka.event;

import org.iotcity.iot.framework.net.event.NetSessionEvent;
import org.iotcity.iot.framework.net.event.NetSessionEventData;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaSessionEvent extends NetSessionEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaSessionEvent(Object source, NetSessionEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
