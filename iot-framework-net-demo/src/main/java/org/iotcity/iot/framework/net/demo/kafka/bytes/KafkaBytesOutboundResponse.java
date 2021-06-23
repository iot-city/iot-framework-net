package org.iotcity.iot.framework.net.demo.kafka.bytes;

import org.iotcity.iot.framework.net.demo.NetDemoResponse;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaOutbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaBytesOutboundResponse extends NetKafkaOutbound<String, byte[], NetDemoResponse> {

	@Override
	public boolean filter(NetKafkaIO<String, byte[]> io, NetDemoResponse data) {
		return false;
	}

	@Override
	public NetMessageStatus send(NetKafkaIO<String, byte[]> io, NetDemoResponse data) {
		return null;
	}

}
