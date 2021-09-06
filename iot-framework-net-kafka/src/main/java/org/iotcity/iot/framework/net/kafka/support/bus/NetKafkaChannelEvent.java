package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetChannelEvent;
import org.iotcity.iot.framework.net.support.bus.NetChannelEventData;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaChannelEvent extends NetChannelEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaChannelEvent(Object source, NetChannelEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
