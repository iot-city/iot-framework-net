package org.iotcity.iot.framework.net.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetMessageStatus;

/**
 * The status callback to execute when the record has been acknowledged by the server.
 * @author ardon
 * @date 2021-09-05
 */
public interface NetKafkaSenderCallback {

	/**
	 * On message status callback after execution.
	 * @param status The message process status (OK or SEND_EXCEPTION).
	 * @param metadata The metadata for the record that was sent (i.e. the partition and offset). An empty metadata with -1 value for all fields except for topicPartition will be returned if an error occurred.
	 * @param exception The exception thrown during processing of this record. Null if no error occurred.
	 */
	void onCallback(NetMessageStatus status, RecordMetadata metadata, Exception exception);

}
