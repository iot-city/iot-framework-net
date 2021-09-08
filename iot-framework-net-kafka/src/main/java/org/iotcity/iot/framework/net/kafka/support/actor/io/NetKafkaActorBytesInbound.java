package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;
import org.iotcity.iot.framework.net.kafka.NetKafkaReader;
import org.iotcity.iot.framework.net.kafka.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestData;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseData;
import org.iotcity.iot.framework.net.serialization.serializable.SerializableHelper;

/**
 * The network kafka actor inbound by using bytes encoding.
 * @author ardon
 * @date 2021-08-29
 */
public final class NetKafkaActorBytesInbound extends NetKafkaInbound<byte[], byte[], NetData> {

	@Override
	public boolean filter(NetKafkaIO<byte[], byte[]> io) {
		return true;
	}

	@Override
	public NetData read(NetKafkaIO<byte[], byte[]> io) throws Exception {
		// Gets the record.
		NetKafkaReader<byte[], byte[]> reader = io.getReader();
		ConsumerRecord<byte[], byte[]> record = reader.getRecord();
		// Gets the record key.
		byte[] keys = record.key();
		if (keys == null || keys.length == 0) return null;
		// Gets the partition information.
		NetKafkaTopicPartition partition = new NetKafkaTopicPartition(record.topic(), record.partition());
		// Gets the message queue.
		String messageID = new String(keys);
		// Deserialize data value.
		Object data = SerializableHelper.deserialize(record.value());
		// Return network data.
		if (data instanceof NetKafkaActorRequestData) {
			// Create inbound request.
			NetKafkaActorRequestData req = (NetKafkaActorRequestData) data;
			return new NetKafkaActorRequest(messageID, partition, req.header, req.command, req.params, req.callback);
		} else {
			// Create inbound response.
			NetKafkaActorResponseData res = (NetKafkaActorResponseData) data;
			return new NetKafkaActorResponse(messageID, partition, res.result, res.data);
		}
	}

}
