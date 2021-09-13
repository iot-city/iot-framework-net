package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetMessageDirection;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetOutboundHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOString;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSender;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSenderCallback;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaSenderResult;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorMessageInfo;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestString;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseString;
import org.iotcity.iot.framework.net.serialization.json.JSON;
import org.iotcity.iot.framework.net.serialization.json.JSONFactory;

/**
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaActorStringOutbound extends NetOutboundHandler<NetKafkaIOString, NetData> {

	@Override
	public boolean filter(NetKafkaIOString io, NetData data) {
		return true;
	}

	@Override
	public NetMessageStatus send(NetKafkaIOString io, NetData data, long timeout) throws Exception {
		// Gets the JSON object.
		JSON json = JSONFactory.getDefaultJSON();
		// Gets the sender object.
		NetKafkaSender<String, String> sender = io.getSender();

		// Check for request.
		if (data.isRequest()) {

			// Gets request object.
			NetKafkaActorRequest request = (NetKafkaActorRequest) data;
			// Gets the partition information.
			NetKafkaTopicPartition partition = request.partition;

			// Gets message ID.
			String messageID = request.getMessageID();
			// Gets the message information.
			NetKafkaActorMessageInfo info = new NetKafkaActorMessageInfo(true, messageID);
			// Gets the record key.
			String key = json.toJSONString(info);

			// Gets the parameter string.
			String params = json.toJSONString(request.params);
			// Gets the callback information.
			NetKafkaTopicPartition callback = request.callback == null ? sender.getCallback() : request.callback;
			// Create request data.
			NetKafkaActorRequestString reqData = new NetKafkaActorRequestString(request.header, request.command, params, callback);
			// Gets the record value.
			String value = json.toJSONString(reqData);

			// Create a message record.
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(partition.topic, partition.partition, key, value);
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
			// Gets the message information.
			NetKafkaActorMessageInfo info = new NetKafkaActorMessageInfo(false, messageID);
			// Gets the record key.
			String key = json.toJSONString(info);

			// Gets the result value.
			String ret = json.toJSONString(response.data);
			// Create response data.
			NetKafkaActorResponseString resData = new NetKafkaActorResponseString(response.result, ret);
			// Gets the record value.
			String value = json.toJSONString(resData);

			// Create a message record.
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(partition.topic, partition.partition, key, value);
			// Send a record to kafka cluster.
			NetKafkaSenderResult result = sender.send(record);
			// Return the status.
			return result.getStatus();

		}
	}

}
