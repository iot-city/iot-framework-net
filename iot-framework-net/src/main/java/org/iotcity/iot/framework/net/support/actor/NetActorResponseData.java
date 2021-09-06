package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

/**
 * Actor response data for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorResponseData implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * The application ID (not null or empty).
	 */
	public String app;
	/**
	 * Response status (refer to ActorResponseStatus.XXXX.ordinal()).
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

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor response data.
	 */
	public NetActorResponseData() {
	}

	/**
	 * Constructor for actor response data.
	 * @param app The application ID (can not be null or empty).
	 * @param status Response status (refer to ActorResponseStatus.XXXX.ordinal()).
	 * @param msg Response message (usually used in response to result prompt, set to null if not required).
	 * @param ref Reference notes (usually used for program debugging, set to null if not required).
	 * @param data The business response data from method (optional, set to null if not required).
	 */
	public NetActorResponseData(String app, int status, String msg, String ref, Serializable data) {
		this.app = app;
		this.status = status;
		this.msg = msg;
		this.ref = ref;
		this.data = data;
	}

}
