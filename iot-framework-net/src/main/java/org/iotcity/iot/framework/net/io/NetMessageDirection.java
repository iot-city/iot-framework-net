package org.iotcity.iot.framework.net.io;

/**
 * The network message data transmission direction.
 * @author ardon
 * @date 2021-06-25
 */
public enum NetMessageDirection {

	/**
	 * The message data from remote end (reading data has failed in this case).
	 */
	FROM_REMOTE_DATA,

	/**
	 * The request message from remote end.
	 */
	FROM_REMOTE_REQUEST,

	/**
	 * The response message from remote end.
	 */
	FROM_REMOTE_RESPONSE,

	/**
	 * The request message to remote end.
	 */
	TO_REMOTE_REQUEST,

	/**
	 * The response message to remote end.
	 */
	TO_REMOTE_RESPONSE,

}
