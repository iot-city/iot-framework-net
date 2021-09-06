package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetSessionEvent;
import org.iotcity.iot.framework.net.support.bus.NetSessionEventData;

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
