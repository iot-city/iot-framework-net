package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetService;

/**
 * The network response result data.
 * @author ardon
 * @date 2021-06-25
 */
public final class NetResponseResult<RES extends NetDataResponse> {

	// --------------------------- Private fields ----------------------------

	/**
	 * The network input and output object (not null).
	 */
	private final NetIO<?, ?> io;
	/**
	 * The message process status.
	 */
	private final NetMessageStatus status;
	/**
	 * The response data object from remote end.
	 */
	private final RES response;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for network response result data.
	 * @param io The network input and output object (required, can not be null).
	 * @param status The message process status (required, can not be null).
	 * @param response The response data object from remote end.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "io" or "status" is null.
	 */
	public NetResponseResult(NetIO<?, ?> io, NetMessageStatus status, RES response) {
		if (io == null || status == null) throw new IllegalArgumentException("Parameter io and status can not be null!");
		this.io = io;
		this.status = status;
		this.response = response;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the network service of response (returns not null).
	 */
	public final NetService getService() {
		return io.getService();
	}

	/**
	 * Gets the network channel of response (returns not null).
	 */
	public final NetChannel getChannel() {
		return io.getChannel();
	}

	/**
	 * Gets the network input and output object of response (returns not null).
	 */
	public final NetIO<?, ?> getNetIO() {
		return io;
	}

	/**
	 * Gets the message process status (returns not null).
	 */
	public final NetMessageStatus getStatus() {
		return status;
	}

	/**
	 * Gets the response data object from remote end (returns null when it does not exist).
	 */
	public final RES getResponse() {
		return response;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{service=\"");
		sb.append(io.getService().getServiceID());
		sb.append("\", channel=\"");
		sb.append(io.getChannel().getChannelID());
		sb.append("\", status=");
		sb.append(status);
		sb.append(", response=");
		JavaHelper.getDataPreview(response, sb);
		sb.append("}");
		return sb.toString();
	}

}
