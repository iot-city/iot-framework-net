package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannelSelector;

/**
 * Network data communication request object.
 * @param <REQ> The request data type.
 * @param <RES> The response data type.
 * @author ardon
 * @date 2021-06-20
 */
public class NetRequester<REQ extends NetDataRequest, RES extends NetDataResponse> {

	/**
	 * Send request data using asynchronous response mode.
	 * @param selector The network channel selector.
	 * @param request The request data object.
	 * @param responser Asynchronous response data processing object.
	 * @param timeout The number of milliseconds of timeout waiting for response data.
	 * @return The message process status.
	 */
	public NetMessageStatus asyncRequest(NetChannelSelector selector, REQ request, NetResponser<REQ, RES> responser, long timeout) {
		return null;
	}

	/**
	 * Send request data using synchronous response mode.
	 * @param selector The network channel selector.
	 * @param request The request data object.
	 * @param timeout The number of milliseconds of timeout waiting for response data.
	 * @return The data object of the synchronous response.
	 */
	public RES syncRequest(NetChannelSelector selector, REQ request, long timeout) {
		return null;
	}

}
