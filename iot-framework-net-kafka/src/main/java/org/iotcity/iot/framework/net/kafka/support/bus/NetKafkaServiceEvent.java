package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.support.bus.NetServiceEvent;
import org.iotcity.iot.framework.net.support.bus.NetServiceEventData;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaServiceEvent extends NetServiceEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaServiceEvent(Object source, NetServiceEventData data) throws IllegalArgumentException {
		super(source, data);
	}

}
