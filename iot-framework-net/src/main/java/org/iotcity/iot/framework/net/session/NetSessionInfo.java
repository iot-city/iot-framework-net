package org.iotcity.iot.framework.net.session;

import java.io.Serializable;

/**
 * The session information data.
 * @author ardon
 * @date 2021-08-29
 */
public class NetSessionInfo implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * The server unique identification of session connection.
	 */
	public String serverID;
	/**
	 * The service unique identification of server.
	 */
	public String serviceID;
	/**
	 * The channel unique identification of service.
	 */
	public String channelID;
	/**
	 * The group unique identification of session manager.
	 */
	public String groupID;
	/**
	 * The session unique identification of session group.
	 */
	public String sessionID;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for session information data.
	 */
	public NetSessionInfo() {
	}

	/**
	 * Constructor for session information data.
	 * @param serverID The server unique identification of session connection.
	 * @param serviceID The service unique identification of server.
	 * @param channelID The channel unique identification of service.
	 * @param groupID The group unique identification of session manager.
	 * @param sessionID The session unique identification of session group.
	 */
	public NetSessionInfo(String serverID, String serviceID, String channelID, String groupID, String sessionID) {
		this.serverID = serverID;
		this.serviceID = serviceID;
		this.channelID = channelID;
		this.groupID = groupID;
		this.sessionID = sessionID;
	}

}
