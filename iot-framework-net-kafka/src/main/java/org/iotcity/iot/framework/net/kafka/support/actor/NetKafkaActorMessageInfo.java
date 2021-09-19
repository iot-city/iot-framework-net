package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * Kafka actor message information data for network transmission.
 * @author ardon
 * @date 2021-09-12
 */
public class NetKafkaActorMessageInfo implements Serializable {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Indicates whether it is request data.
	 */
	public boolean request;
	/**
	 * The message ID of the paired message request and response.
	 */
	public String messageID;

	/**
	 * Constructor for kafka message information data.
	 */
	public NetKafkaActorMessageInfo() {
	}

	/**
	 * Constructor for kafka message information data.
	 * @param request Indicates whether it is request data.
	 * @param messageID The message ID of the paired message request and response.
	 */
	public NetKafkaActorMessageInfo(boolean request, String messageID) {
		this.request = request;
		this.messageID = messageID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{request=");
		sb.append(request);
		sb.append(", messageID=");
		JavaHelper.getDataPreview(messageID, sb);
		sb.append("}");
		return sb.toString();
	}

}
