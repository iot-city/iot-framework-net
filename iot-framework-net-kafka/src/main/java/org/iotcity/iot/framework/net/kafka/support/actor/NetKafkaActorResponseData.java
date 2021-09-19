package org.iotcity.iot.framework.net.kafka.support.actor;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.support.actor.NetActorResult;

/**
 * Kafka actor response data for network transmission.
 * @author ardon
 * @date 2021-09-08
 */
public final class NetKafkaActorResponseData implements Serializable {

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
	public Serializable data;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for kafka actor response data.
	 */
	public NetKafkaActorResponseData() {
	}

	/**
	 * Constructor for kafka actor response data.
	 * @param result Actor response status result (required, can not be null).
	 * @param data The actor response data from actor method invoking (optional, set it to null if there is no response data).
	 */
	public NetKafkaActorResponseData(NetActorResult result, Serializable data) {
		this.result = result;
		this.data = data;
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{result=");
		sb.append(result);
		sb.append(", data=");
		JavaHelper.getDataPreview(data, sb);
		sb.append("}");
		return sb.toString();
	}

}
