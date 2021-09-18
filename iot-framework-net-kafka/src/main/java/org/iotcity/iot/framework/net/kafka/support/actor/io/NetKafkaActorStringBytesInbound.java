package org.iotcity.iot.framework.net.kafka.support.actor.io;

import java.io.Serializable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOStringBytes;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaReader;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestData;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseData;
import org.iotcity.iot.framework.net.serialization.bytes.BYTESFactory;

/**
 * The network kafka actor inbound by using string key and bytes value encoding.
 * @author ardon
 * @date 2021-09-05
 */
public final class NetKafkaActorStringBytesInbound extends NetInboundHandler<NetKafkaIOStringBytes, NetData> {

	@Override
	public boolean filter(NetKafkaIOStringBytes io) {
		return true;
	}

	@Override
	public NetData read(NetKafkaIOStringBytes io) throws Exception {
		// Gets the record.
		NetKafkaReader<String, byte[]> reader = io.getReader();
		ConsumerRecord<String, byte[]> record = reader.getRecord();
		// Gets the message queue.
		String messageID = record.key();
		if (StringHelper.isEmpty(messageID)) return null;
		// Gets the partition information.
		NetKafkaTopicPartition partition = new NetKafkaTopicPartition(record.topic(), record.partition());
		// Deserialize data value.
		Serializable data = BYTESFactory.getDefaultBytes().deserialize(record.value());
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
