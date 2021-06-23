package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetResponseCallback;

/**
 * The network asynchronous request data event.
 * @author ardon
 * @date 2021-06-21
 */
public class NetMessageAsyncEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The network I/O object.
	 */
	private final NetIO<?, ?, ?> io;
	/**
	 * Network communication response callback object (not null).
	 */
	private final NetResponseCallback<NetDataResponse> callbacker;

	/**
	 * Constructor for network asynchronous request data event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @param callbacker Network communication response callback object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	public NetMessageAsyncEvent(Object source, NetIO<?, ?, ?> io, NetData request, NetResponseCallback<NetDataResponse> callbacker) throws IllegalArgumentException {
		super(source, request, true);
		this.io = io;
		this.callbacker = callbacker;
	}

	/**
	 * Gets the network I/O object (returns not null).
	 */
	public NetIO<?, ?, ?> getNetIO() {
		return io;
	}

	/**
	 * Callback response data processing.
	 * @param response The response data object (set to null when there is no callback data).
	 * @return The message process status.
	 */
	public NetMessageStatus callback(NetDataResponse response) {
		return callbacker.callback(response);
	}

}
