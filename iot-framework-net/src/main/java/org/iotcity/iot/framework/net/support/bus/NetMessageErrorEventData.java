package org.iotcity.iot.framework.net.support.bus;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetMessageDirection;
import org.iotcity.iot.framework.net.io.NetMessageStatus;

/**
 * The network message processing error data.
 * @author ardon
 * @date 2021-06-24
 */
public class NetMessageErrorEventData {

	/**
	 * The network message data transmission direction.
	 */
	private final NetMessageDirection direction;
	/**
	 * The message I/O object (not null).
	 */
	private final NetIO<?, ?> messageIO;
	/**
	 * The request data object (null value when there is no data).
	 */
	private final NetData requestData;
	/**
	 * The response data object (null value when there is no data).
	 */
	private final NetData responseData;
	/**
	 * Array of exception objects encountered when processing inbound or outbound data (null value when there is no exception).
	 */
	private final Exception[] exceptions;
	/**
	 * The message processing error status (not null).
	 */
	private final NetMessageStatus errorStatus;

	/**
	 * Constructor for network message processing error data.
	 * @param direction The network message data transmission direction (required, not null).
	 * @param messageIO The message I/O object (required, not null).
	 * @param requestData The request data object (set it to null when there is no request data).
	 * @param responseData The response data object (set it to null when there is no response data).
	 * @param exceptions Array of exception objects encountered when processing inbound or outbound data (set it to null when there is no exception).
	 * @param errorStatus The message processing error status (required, not null).
	 */
	public NetMessageErrorEventData(NetMessageDirection direction, NetIO<?, ?> messageIO, NetData requestData, NetData responseData, Exception[] exceptions, NetMessageStatus errorStatus) {
		this.direction = direction;
		this.messageIO = messageIO;
		this.requestData = requestData;
		this.responseData = responseData;
		this.exceptions = exceptions;
		this.errorStatus = errorStatus;
	}

	/**
	 * Gets the network message data transmission direction.
	 */
	public final NetMessageDirection getDirection() {
		return direction;
	}

	/**
	 * Gets the message I/O object (not null).
	 */
	public final NetIO<?, ?> getMessageIO() {
		return messageIO;
	}

	/**
	 * Gets the request data object (returns null when there is no request data).
	 */
	public final NetData getRequestData() {
		return requestData;
	}

	/**
	 * Gets the response data object (returns null when there is no response data).
	 */
	public final NetData getResponseData() {
		return responseData;
	}

	/**
	 * Indicates whether an exception was encountered when processing inbound or outbound data.
	 */
	public final boolean hasException() {
		return exceptions != null && exceptions.length > 0;
	}

	/**
	 * Gets the array of exception objects encountered when processing inbound or outbound data (returns null when there is no exception).
	 */
	public final Exception[] getExceptions() {
		return exceptions;
	}

	/**
	 * Gets the message processing error status (returns not null).
	 */
	public final NetMessageStatus getErrorStatus() {
		return errorStatus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{service=\"");
		sb.append(messageIO.getService().getServiceID());
		sb.append("\", channel=\"");
		sb.append(messageIO.getChannel().getChannelID());
		sb.append("\", direction=");
		sb.append(direction);
		sb.append(", request=");
		JavaHelper.getDataPreview(requestData, sb);
		sb.append(", response=");
		JavaHelper.getDataPreview(responseData, sb);
		sb.append(", exceptions=");
		sb.append(exceptions == null ? 0 : exceptions.length);
		sb.append(", errorStatus=");
		sb.append(errorStatus);
		sb.append("}");
		return sb.toString();
	}

}
