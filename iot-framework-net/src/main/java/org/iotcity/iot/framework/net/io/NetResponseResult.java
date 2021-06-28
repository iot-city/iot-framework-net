package org.iotcity.iot.framework.net.io;

/**
 * The network response result data.
 * @author ardon
 * @date 2021-06-25
 */
public class NetResponseResult<RES extends NetDataResponse> {

	/**
	 * The message process status.
	 */
	private final NetMessageStatus status;
	/**
	 * The response data object from remote end.
	 */
	private final RES response;

	/**
	 * Constructor for
	 * @param status The message process status.
	 * @param response The response data object from remote end.
	 */
	public NetResponseResult(NetMessageStatus status, RES response) {
		this.status = status;
		this.response = response;
	}

	/**
	 * Gets the message process status.
	 */
	public NetMessageStatus getStatus() {
		return status;
	}

	/**
	 * Gets the response data object from remote end.
	 */
	public RES getResponse() {
		return response;
	}

}
