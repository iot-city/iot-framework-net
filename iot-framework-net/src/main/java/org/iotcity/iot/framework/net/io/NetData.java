package org.iotcity.iot.framework.net.io;

/**
 * The network data for inbound and outbound.
 * @author ardon
 * @date 2021-06-16
 */
public class NetData {

	/**
	 * Indicates whether it is request data.
	 */
	private final boolean request;

	/**
	 * Constructor for network data.
	 * @param isRequest Set to true when used as request data and false when used as response data.
	 */
	NetData(boolean isRequest) {
		this.request = isRequest;
	}

	/**
	 * Whether this data is request data.
	 * @return Return true when used as request data and false when used as response data.
	 */
	public boolean isRequest() {
		return request;
	}

}
