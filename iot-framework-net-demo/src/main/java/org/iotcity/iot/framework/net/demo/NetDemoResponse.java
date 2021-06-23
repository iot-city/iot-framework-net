package org.iotcity.iot.framework.net.demo;

import java.io.Serializable;

import org.iotcity.iot.framework.net.io.NetDataResponse;

/**
 * @author ardon
 * @date 2021-06-15
 */
public class NetDemoResponse extends NetDataResponse {

	/**
	 * Response status (refer to ActorResponseStatus.XXXX).
	 */
	public int status;
	/**
	 * Response message (usually used in response to result prompt, set to null if not required).
	 */
	public String msg;
	/**
	 * Reference notes (usually used for program debugging, set to null if not required).
	 */
	public String ref;
	/**
	 * The business response data from method (optional).
	 */
	public Serializable data;

}
