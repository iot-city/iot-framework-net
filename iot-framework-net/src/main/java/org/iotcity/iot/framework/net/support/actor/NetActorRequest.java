package org.iotcity.iot.framework.net.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.io.NetDataAsyncRequest;

/**
 * Actor request for network transmission.
 * @author ardon
 * @date 2021-08-29
 */
public class NetActorRequest extends NetDataAsyncRequest {

	// --------------------------- Public fields ----------------------------

	/**
	 * The actor header information (never null).
	 */
	public final NetActorHeader header;
	/**
	 * The actor command information (never null).
	 */
	public final NetActorCommand command;
	/**
	 * An array of parameters that be used to invoke the method (null if there is no parameter).
	 */
	public final Serializable[] params;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor request.
	 * @param messageID The message ID of the paired message request and response (required, can not be null or an empty string).
	 * @param header The actor header information (required, can not be null).
	 * @param command The actor command information (required, can not be null).
	 * @param params An array of parameters that be used to invoke the method (optional, set it to null if there is no parameter).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "messageID", "header" or "command" is null or empty.
	 */
	public NetActorRequest(String messageID, NetActorHeader header, NetActorCommand command, Serializable[] params) throws IllegalArgumentException {
		super(messageID);
		if (header == null || command == null) throw new IllegalArgumentException("Parameter header and command can not be null!");
		this.header = header;
		this.command = command;
		this.params = params;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{messageID=\"");
		sb.append(messageID);
		sb.append("\", header=");
		sb.append(header.toString());
		sb.append(", command=");
		sb.append(command.toString());
		sb.append(", params=");
		JavaHelper.getArrayPreview(params, sb, false);
		sb.append("}");
		return sb.toString();
	}

}
