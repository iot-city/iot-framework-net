package org.iotcity.iot.framework.net.kafka.event;

import org.iotcity.iot.framework.net.event.NetMessageSyncEvent;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetIO;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaMessageSyncEvent extends NetMessageSyncEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaMessageSyncEvent(Object source, NetIO<?, ?, ?> io, NetData request) throws IllegalArgumentException {
		super(source, io, request);
	}

}
