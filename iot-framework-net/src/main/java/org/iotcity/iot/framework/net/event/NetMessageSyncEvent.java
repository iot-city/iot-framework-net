package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;

/**
 * The network synchronous request data event.
 * @author ardon
 * @date 2021-06-21
 */
public class NetMessageSyncEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The network input and output object.
	 */
	private final NetIO<?, ?, ?> io;
	/**
	 * The response data object.
	 */
	private NetDataResponse response;

	/**
	 * Constructor for network synchronous request data event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	public NetMessageSyncEvent(Object source, NetIO<?, ?, ?> io, NetData request) throws IllegalArgumentException {
		super(source, request, true);
		this.io = io;
	}

	/**
	 * Gets the network I/O object (returns not null).
	 */
	public NetIO<?, ?, ?> getNetIO() {
		return io;
	}

	/**
	 * Gets the response data object (returns null if no response data).
	 */
	public NetDataResponse getResponse() {
		return response;
	}

	/**
	 * Set the response data object.
	 * @param response The response data object.
	 */
	public void setResponse(NetDataResponse response) {
		this.response = response;
	}

}
