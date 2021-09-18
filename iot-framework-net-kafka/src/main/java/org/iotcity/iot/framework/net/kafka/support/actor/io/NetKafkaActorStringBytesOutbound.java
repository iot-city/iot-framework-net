package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetMessageDirection;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetOutboundHandler;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOStringBytes;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSender;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSenderCallback;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSenderResult;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestData;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseData;
import org.iotcity.iot.framework.net.serialization.bytes.BYTESFactory;

/**
 * The network kafka actor outbound by using string key and bytes value encoding.
 * @author ardon
 * @date 2021-09-05
 */
public final class NetKafkaActorStringBytesOutbound extends NetOutboundHandler<NetKafkaIOStringBytes, NetData> {

	@Override
	public boolean filter(NetKafkaIOStringBytes io, NetData data) {
		return true;
	}

	@Override
	public NetMessageStatus send(NetKafkaIOStringBytes io, NetData data, long timeout) throws Exception {
		// Gets the sender object.
		NetKafkaSender<String, byte[]> sender = io.getSender();
		// Check request flag.
		if (data.isRequest()) {

			// Gets request object.
			NetKafkaActorRequest request = (NetKafkaActorRequest) data;
			// Gets the partition information.
			NetKafkaTopicPartition partition = request.partition;
			// Gets message key.
			String messageID = request.getMessageID();
			// Gets the callback information.
			NetKafkaTopicPartition callback = request.callback == null ? sender.getCallback() : request.callback;
			// Create request data.
			NetKafkaActorRequestData reqData = new NetKafkaActorRequestData(request.header, request.command, request.params, callback);
			// Gets message value.
			byte[] values = BYTESFactory.getDefaultBytes().serialize(reqData);
			// Create a message record.
			ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(partition.topic, partition.partition, messageID, values);
			// Send a record to kafka cluster.
			NetKafkaSenderResult result = sender.send(record, new NetKafkaSenderCallback() {

				@Override
				public void onCallback(NetMessageStatus status, RecordMetadata metadata, Exception exception) {
					// Determines whether to send data successfully.
					if (status == NetMessageStatus.OK) {
						// Callback response status on success if the callback is not required.
						if (callback == null) io.getResponser().tryCallback(NetMessageDirection.TO_REMOTE_REQUEST, io, messageID, NetKafkaActorResponse.class, status, null);
					} else {
						// Callback response status on failure.
						io.getResponser().tryCallback(NetMessageDirection.TO_REMOTE_REQUEST, io, messageID, NetKafkaActorResponse.class, status, null);
					}
				}

			});
			return result.getStatus();

		} else {

			// Gets response object.
			NetKafkaActorResponse response = (NetKafkaActorResponse) data;
			// Gets the partition information.
			NetKafkaTopicPartition partition = response.partition;
			// Gets message key.
			String messageID = response.getMessageID();
			// Create response data.
			NetKafkaActorResponseData resData = new NetKafkaActorResponseData(response.result, response.data);
			// Gets message value.
			byte[] values = BYTESFactory.getDefaultBytes().serialize(resData);
			// Create a message record.
			ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(partition.topic, partition.partition, messageID, values);
			// Send a record to kafka cluster.
			NetKafkaSenderResult result = sender.send(record);
			// Return the status.
			return result.getStatus();

		}
	}

}
