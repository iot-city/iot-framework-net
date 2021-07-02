package org.iotcity.iot.framework.net.io;

/**
 * The network message status callback interface.
 * @author ardon
 * @date 2021-06-29
 */
public interface NetMessageStatusCallback {

	/**
	 * On message status callback after execution.
	 * @param status The message process status.
	 */
	void onCallback(NetMessageStatus status);

}
