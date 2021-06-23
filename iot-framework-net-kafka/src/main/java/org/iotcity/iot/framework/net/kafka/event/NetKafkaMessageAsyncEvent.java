package org.iotcity.iot.framework.net.kafka.event;

import org.iotcity.iot.framework.net.event.NetMessageAsyncEvent;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetResponseCallback;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaMessageAsyncEvent extends NetMessageAsyncEvent {

	private static final long serialVersionUID = 1L;

	public NetKafkaMessageAsyncEvent(Object source, NetIO<?, ?, ?> io, NetData request, NetResponseCallback<NetDataResponse> callbacker) throws IllegalArgumentException {
		super(source, io, request, callbacker);
	}

}
