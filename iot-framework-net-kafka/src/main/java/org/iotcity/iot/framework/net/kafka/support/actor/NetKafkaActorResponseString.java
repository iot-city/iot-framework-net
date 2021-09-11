package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.support.actor.NetActorResult;

/**
 * Kafka actor response string data for network transmission.
 * @author ardon
 * @date 2021-09-12
 */
public class NetKafkaActorResponseString implements Serializable {

	// --------------------------- Static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Public fields ----------------------------

	/**
	 * Actor response result (never null).
	 */
	public NetActorResult result;
	/**
	 * The response data from actor method invoking (null if there is no response data).
	 */
	public String data;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor response string data.
	 */
	public NetKafkaActorResponseString() {
	}

	/**
	 * Constructor for kafka actor response string data.
	 * @param result Actor response status result (required, can not be null).
	 * @param data The actor response data from actor method invoking (optional, set it to null if there is no response data).
	 */
	public NetKafkaActorResponseString(NetActorResult result, String data) {
		this.result = result;
		this.data = data;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{result=");
		sb.append(result.toString());
		sb.append(", data=");
		JavaHelper.getDataPreview(data, sb);
		sb.append("}");
		return sb.toString();
	}

}
