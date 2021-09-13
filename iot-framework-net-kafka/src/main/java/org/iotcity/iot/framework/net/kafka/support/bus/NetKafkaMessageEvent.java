package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.support.bus.NetMessageEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageEventCallback;

/**
 * The kafka message request event (this event can not be cancelled).
 * @author ardon
 * @date 2021-06-22
 */
public final class NetKafkaMessageEvent extends NetMessageEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for kafka message request event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @param callback Network communication response callback object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	public NetKafkaMessageEvent(Object source, NetIO<?, ?> io, NetData request, NetMessageEventCallback callback) throws IllegalArgumentException {
		super(source, io, request, callback);
	}

}
