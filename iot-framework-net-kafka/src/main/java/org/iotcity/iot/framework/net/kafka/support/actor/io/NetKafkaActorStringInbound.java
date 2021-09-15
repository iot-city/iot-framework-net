package org.iotcity.iot.framework.net.kafka.support.actor.io;

import java.io.Serializable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;
import org.iotcity.iot.framework.net.kafka.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOString;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaReader;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorMessageInfo;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestString;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseString;
import org.iotcity.iot.framework.net.serialization.json.JSON;
import org.iotcity.iot.framework.net.serialization.json.JSONFactory;
import org.iotcity.iot.framework.net.support.actor.NetActorClassFactory;

/**
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaActorStringInbound extends NetInboundHandler<NetKafkaIOString, NetData> {

	@Override
	public boolean filter(NetKafkaIOString io) {
		return true;
	}

	@Override
	public NetData read(NetKafkaIOString io) throws Exception {
		// Gets the actor class factory.
		NetActorClassFactory classFactory = io.getClassFactory();
		if (classFactory == null) return null;

		// Gets the record.
		NetKafkaReader<String, String> reader = io.getReader();
		ConsumerRecord<String, String> record = reader.getRecord();

		// Gets the message queue.
		String key = record.key();
		if (StringHelper.isEmpty(key)) return null;
		// Gets the JSON object.
		JSON json = JSONFactory.getDefaultJSON();
		// Gets the message information.
		NetKafkaActorMessageInfo info = json.toObject(NetKafkaActorMessageInfo.class, key);
		if (info == null) return null;
		// Gets the partition information.
		NetKafkaTopicPartition partition = new NetKafkaTopicPartition(record.topic(), record.partition());

		// Check for request mode.
		if (info.request) {

			// Create inbound request.
			NetKafkaActorRequestString req = json.toObject(NetKafkaActorRequestString.class, record.value());
			if (req == null) return null;
			Serializable[] params = json.toArray(Serializable.class, classFactory.getParameterTypes(req.command), req.params);
			// Return request data.
			return new NetKafkaActorRequest(info.messageID, partition, req.header, req.command, params, req.callback);

		} else {

			// Gets the request data.
			NetKafkaActorRequest req = io.getCallbackRequest(info.messageID, NetKafkaActorRequest.class, NetKafkaActorResponse.class);
			if (req == null) return null;
			// Create inbound response.
			NetKafkaActorResponseString res = json.toObject(NetKafkaActorResponseString.class, record.value());
			if (res == null) return null;
			// Gets the data object.
			Serializable data = (Serializable) json.toObject(classFactory.getReturnType(req.command), res.data);
			// Return response data.
			return new NetKafkaActorResponse(info.messageID, partition, res.result, data);

		}
	}

}
