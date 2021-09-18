package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOBytes;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaReader;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestData;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseData;
import org.iotcity.iot.framework.net.serialization.bytes.BYTESFactory;

/**
 * The network kafka actor inbound by using bytes encoding.
 * @author ardon
 * @date 2021-08-29
 */
public final class NetKafkaActorBytesInbound extends NetInboundHandler<NetKafkaIOBytes, NetData> {

	@Override
	public boolean filter(NetKafkaIOBytes io) {
		return true;
	}

	@Override
	public NetData read(NetKafkaIOBytes io) throws Exception {
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
		Object data = BYTESFactory.getDefaultBytes().deserialize(record.value());
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
