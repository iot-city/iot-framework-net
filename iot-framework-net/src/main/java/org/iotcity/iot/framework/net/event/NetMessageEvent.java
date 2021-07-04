package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetMessageStatus;

/**
 * The network message request event (this event can not be cancelled).
 * @author ardon
 * @date 2021-06-21
 */
public class NetMessageEvent extends BusEvent {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The response callback lock.
	 */
	private final Object lock = new Object();
	/**
	 * The network I/O object.
	 */
	private final NetIO<?, ?> io;
	/**
	 * Network communication response callback object (not null).
	 */
	private final NetMessageEventCallback callback;
	/**
	 * Indicates whether the send response method has been executed.
	 */
	private boolean sentResponse = false;
	/**
	 * The sending status of the response data.
	 */
	private NetMessageStatus sentStatus;
	/**
	 * The business logic processing status of the message.
	 */
	private NetMessageStatus businessStatus;
	/**
	 * The business response data object.
	 */
	private NetDataResponse businessResponse;

	/**
	 * Constructor for network message request event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @param callback Network communication response callback object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	public NetMessageEvent(Object source, NetIO<?, ?> io, NetData request, NetMessageEventCallback callback) throws IllegalArgumentException {
		super(source, request, false);
		this.io = io;
		this.callback = callback;
	}

	/**
	 * Gets the network I/O object (returns not null).
	 */
	public NetIO<?, ?> getNetIO() {
		return io;
	}

	/**
	 * Indicates whether the asynchronous processing mode is used.
	 */
	public boolean isAsynchronous() {
		return io.isAsynchronous();
	}

	/**
	 * Indicates whether the send response method has been executed.
	 */
	public boolean isSentResponse() {
		return sentResponse;
	}

	/**
	 * Gets the sending status of the response data (returns null if the response sending is not executed).
	 */
	public NetMessageStatus getSentStatus() {
		return sentStatus;
	}

	/**
	 * Gets the business logic processing status of the response (returns null if the response sending is not executed).
	 */
	public NetMessageStatus getBusinessStatus() {
		return businessStatus;
	}

	/**
	 * Gets the business response data object (returns null if the response sending is not executed).
	 */
	public NetDataResponse getBusinessResponse() {
		return businessResponse;
	}

	/**
	 * Send a response message to the remote end.
	 * @param status The business logic processing status of the response (required, can not be null).
	 * @param response The response data object (set to null when there is no callback data).
	 * @return The sending status of the response data.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "status" is null.
	 */
	public NetMessageStatus sendResponse(NetMessageStatus status, NetDataResponse response) throws IllegalArgumentException {
		if (status == null) throw new IllegalArgumentException("Parameter status can not be null!");
		if (sentResponse) return NetMessageStatus.DUPLICATED;
		synchronized (lock) {
			if (sentResponse) return NetMessageStatus.DUPLICATED;
			sentResponse = true;
			businessStatus = status;
			businessResponse = response;
			// Callback response to send the message.
			sentStatus = callback.onCallback(this, status, response);
		}
		return sentStatus;
	}

}
