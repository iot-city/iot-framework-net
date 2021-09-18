package org.iotcity.iot.framework.net.kafka.io;

import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetMessageStatus;

/**
 * The kafka sender result after execution.
 * @author ardon
 * @date 2021-09-05
 */
public final class NetKafkaSenderResult {

	/**
	 * Network message status.
	 */
	private final NetMessageStatus status;
	/**
	 * A Future represents the result of an asynchronous computation.
	 */
	private final Future<RecordMetadata> future;

	/**
	 * Constructor for kafka sender result after execution.
	 * @param status Network message status.
	 * @param future A Future represents the result of an asynchronous computation.
	 */
	public NetKafkaSenderResult(NetMessageStatus status, Future<RecordMetadata> future) {
		this.status = status;
		this.future = future;
	}

	/**
	 * Gets the network message status.
	 */
	public final NetMessageStatus getStatus() {
		return status;
	}

	/**
	 * Gets the future represents the result of an asynchronous computation (returns null if there is no future in result).
	 */
	public final Future<RecordMetadata> getFuture() {
		return future;
	}

}
