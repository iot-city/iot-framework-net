package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetMessageStatus;

/**
 * Network event response callback object.
 * @author ardon
 * @date 2021-06-22
 */
public interface NetMessageEventCallback {

	/**
	 * On event response data callback processing.
	 * @param event The message event object.
	 * @param status The business logic processing status of the response.
	 * @param response The response data object (it is null value when there is no callback data).
	 * @return The sending status of the response data.
	 */
	NetMessageStatus onCallback(NetMessageEvent event, NetMessageStatus status, NetDataResponse response);

}
