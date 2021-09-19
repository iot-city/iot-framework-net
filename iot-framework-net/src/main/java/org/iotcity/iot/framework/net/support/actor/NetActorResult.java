package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * Actor response result data for network transmission.
 * @author ardon
 * @date 2021-09-07
 */
public class NetActorResult implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

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

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor response result.
	 */
	public NetActorResult() {
	}

	/**
	 * Constructor for actor response result.
	 * @param status Response status (refer to ActorResponseStatus.XXXX.ordinal()).
	 * @param msg Response message (usually used in response to result prompt, set to null if not required).
	 * @param ref Reference notes (usually used for program debugging, set to null if not required).
	 */
	public NetActorResult(int status, String msg, String ref) {
		this.status = status;
		this.msg = msg;
		this.ref = ref;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{status=");
		sb.append(status);
		sb.append(", msg=");
		JavaHelper.getDataPreview(msg, sb);
		sb.append(", ref=");
		JavaHelper.getDataPreview(ref, sb);
		sb.append("}");
		return sb.toString();
	}

}
