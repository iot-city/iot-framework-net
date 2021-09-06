package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.core.util.DataMap;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

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
	 * The network header map object.
	 */
	private DataMap headers = null;

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
	public final boolean isRequest() {
		return request;
	}

	/**
	 * Gets the network header map object.
	 */
	public final DataMap getHeaders() {
		if (headers != null) return headers;
		headers = new DataMap(JavaHelper.getMapInitialCapacity(3));
		return headers;
	}

}
