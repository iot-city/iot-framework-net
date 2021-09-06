package org.iotcity.iot.framework.net.kafka.support.actor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;
import org.iotcity.iot.framework.net.kafka.NetKafkaReader;
import org.iotcity.iot.framework.net.serialization.serializable.SerializableHelper;
import org.iotcity.iot.framework.net.support.actor.NetActorRequestData;
import org.iotcity.iot.framework.net.support.actor.NetActorResponseData;

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
		// Gets the message queue.
		String messageQueue = new String(keys);
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
