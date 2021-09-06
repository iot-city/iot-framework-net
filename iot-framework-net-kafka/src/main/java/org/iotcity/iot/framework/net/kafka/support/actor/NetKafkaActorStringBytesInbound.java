package org.iotcity.iot.framework.net.kafka.support.actor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;
import org.iotcity.iot.framework.net.kafka.NetKafkaReader;
import org.iotcity.iot.framework.net.serialization.serializable.SerializableHelper;
import org.iotcity.iot.framework.net.support.actor.NetActorRequestData;
import org.iotcity.iot.framework.net.support.actor.NetActorResponseData;

/**
 * The network kafka actor inbound by using string key and bytes value encoding.
 * @author ardon
 * @date 2021-09-05
 */
public final class NetKafkaActorStringBytesInbound extends NetKafkaInbound<String, byte[], NetData> {

	@Override
	public boolean filter(NetKafkaIO<String, byte[]> io) {
		return true;
	}

	@Override
	public NetData read(NetKafkaIO<String, byte[]> io) throws Exception {
		// Gets the record.
		NetKafkaReader<String, byte[]> reader = io.getReader();
		ConsumerRecord<String, byte[]> record = reader.getRecord();
		// Gets the message queue.
		String messageQueue = record.key();
		if (StringHelper.isEmpty(messageQueue)) return null;
		// Deserialize data value.
		Object data = SerializableHelper.deserialize(record.value());
		// Return network data.
		if (data instanceof NetActorRequestData) {
			return new NetKafkaActorRequest(messageQueue, (NetActorRequestData) data, record.partition());
		} else {
			return new NetKafkaActorResponse(messageQueue, (NetActorResponseData) data, record.partition());
		}
	}

}
