package org.iotcity.iot.framework.net.io;

/**
 * Network communication response callback object.
 * @param <RES> The response data type.
 * @author ardon
 * @date 2021-06-22
 */
public interface NetResponseCallback<RES extends NetDataResponse> {

	/**
	 * Callback response data processing.
	 * @param response The response data object.
	 * @return The message process status.
	 */
	NetMessageStatus callback(RES response);

}
