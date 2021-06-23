package org.iotcity.iot.framework.net.kafka.event;

import org.iotcity.iot.framework.net.event.NetChannelEvent;
import org.iotcity.iot.framework.net.event.NetChannelEventData;

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
