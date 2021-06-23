package org.iotcity.iot.framework.net.io;

/**
 * Asynchronous response data processing object.
 * @param <REQ> The request data type.
 * @param <RES> The response data type.
 * @author ardon
 * @date 2021-06-22
 */
public interface NetResponser<REQ extends NetDataRequest, RES extends NetDataResponse> extends NetResponseCallback<RES> {

	/**
	 * Gets the request unique key for pairing request and response data before sending request data.
	 * @param request The request data object.
	 * @return The unique key for pairing request and response data.
	 */
	String getCallbackKey(REQ request);

	/**
	 * Gets the response unique key for pairing request and response data when response data is received.
	 * @param response The response data object.
	 * @return The unique key for pairing request and response data.
	 */
	String getCallbackKey(RES response);

}
