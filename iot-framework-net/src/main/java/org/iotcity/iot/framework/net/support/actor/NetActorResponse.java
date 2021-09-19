package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.io.NetDataAsyncResponse;

/**
 * Actor response for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorResponse extends NetDataAsyncResponse {

	// --------------------------- Public fields ----------------------------

	/**
	 * Actor response result (never null).
	 */
	public final NetActorResult result;
	/**
	 * The response data from actor method invoking (null if there is no response data).
	 */
	public final Serializable data;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor response.
	 * @param messageID The message ID of the paired message request and response (required, can not be null or an empty string).
	 * @param result Actor response status result (required, can not be null).
	 * @param data The actor response data from actor method invoking (optional, set it to null if there is no response data).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "messageID", "result" is null or empty.
	 */
	public NetActorResponse(String messageID, NetActorResult result, Serializable data) throws IllegalArgumentException {
		super(messageID);
		if (result == null) throw new IllegalArgumentException("Parameter result can not be null!");
		this.result = result;
		this.data = data;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{messageID=\"");
		sb.append(messageID);
		sb.append("\", result=");
		sb.append(result);
		sb.append(", data=");
		JavaHelper.getDataPreview(data, sb);
		sb.append("}");
		return sb.toString();
	}

}
