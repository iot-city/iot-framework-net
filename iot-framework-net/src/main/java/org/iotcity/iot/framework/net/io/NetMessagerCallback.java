package org.iotcity.iot.framework.net.io;

/**
 * The network message data callback interface.
 * @author ardon
 * @date 2021-09-16
 */
public interface NetMessagerCallback {

	/**
	 * On message data read.
	 * @param data The network data (never null).
	 */
	void onRead(NetData data);

}
