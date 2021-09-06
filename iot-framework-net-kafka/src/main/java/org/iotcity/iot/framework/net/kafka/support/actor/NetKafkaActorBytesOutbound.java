package org.iotcity.iot.framework.net.kafka.support.actor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetMessageDirection;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaOutbound;
import org.iotcity.iot.framework.net.kafka.NetKafkaSender;
import org.iotcity.iot.framework.net.kafka.NetKafkaSenderCallback;
import org.iotcity.iot.framework.net.kafka.NetKafkaSenderResult;
import org.iotcity.iot.framework.net.serialization.serializable.SerializableHelper;
import org.iotcity.iot.framework.net.support.actor.NetActorRequestData;
import org.iotcity.iot.framework.net.support.actor.NetActorResponse;
import org.iotcity.iot.framework.net.support.actor.NetActorResponseData;

/**
 * The network kafka actor outbound by using bytes encoding.
 * @author ardon
 * @date 2021-08-29
 */
public class NetKafkaActorBytesOutbound extends NetKafkaOutbound<byte[], byte[], NetData> {

	@Override
	public boolean filter(NetKafkaIO<byte[], byte[]> io, NetData data) {
		return true;
	}

	@Override
	public NetMessageStatus send(NetKafkaIO<byte[], byte[]> io, NetData data, long timeout) throws Exception {
		NetKafkaSender<byte[], byte[]> sender = io.getSender();
		if (data.isRequest()) {

			// Gets request object.
			NetKafkaActorRequest request = (NetKafkaActorRequest) data;
			NetActorRequestData reqData = request.getData();
			// Build topic string.
			String topic = "APP-" + reqData.app.toUpperCase();
			// Gets message key.
			String messageQueue = request.getMessageQueue();
			byte[] keys = messageQueue.getBytes();
			// Gets message value.
			byte[] values = SerializableHelper.serialize(reqData);
			// Send a record to kafka cluster.
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, request.getPartition(), keys, values);
			NetKafkaSenderResult result = sender.send(record, new NetKafkaSenderCallback() {

				@Override
				public void onCallback(NetMessageStatus status, RecordMetadata metadata, Exception exception) {
					// Determines whether to send data successfully.
					if (status == NetMessageStatus.OK) return;
					// Callback response status on failure.
					io.getResponser().tryCallback(NetMessageDirection.TO_REMOTE_REQUEST, io, messageQueue, NetActorResponse.class, status, null);
				}

			});
			return result.getStatus();

		} else {

			// Gets response object.
			NetKafkaActorResponse response = (NetKafkaActorResponse) data;
			NetActorResponseData resData = response.getData();
			// Build topic string.
			String topic = "APP-" + resData.app.toUpperCase();
			// Gets message key.
			String messageQueue = response.getMessageQueue();
			byte[] keys = messageQueue.getBytes();
			// Gets message value.
			byte[] values = SerializableHelper.serialize(resData);
			// Send a record to kafka cluster.
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, response.getPartition(), keys, values);
			NetKafkaSenderResult result = sender.send(record);
			return result.getStatus();

		}
	}

}
