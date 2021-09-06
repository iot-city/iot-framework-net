package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEventData;

/**
 * @author ardon
 * @date 2021-06-28
 */
public class NetKafkaMessageErrorEvent extends NetMessageErrorEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaMessageErrorEvent(Object source, NetMessageErrorEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
