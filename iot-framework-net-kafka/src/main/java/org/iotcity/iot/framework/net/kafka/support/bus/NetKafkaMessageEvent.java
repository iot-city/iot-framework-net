package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.support.bus.NetMessageEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageEventCallback;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaMessageEvent extends NetMessageEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaMessageEvent(Object source, NetIO<?, ?> io, NetData request, NetMessageEventCallback callback) throws IllegalArgumentException {
		super(source, io, request, callback);
	}

}
