package org.iotcity.iot.framework.net.demo.kafka.bytes;

import org.iotcity.iot.framework.net.demo.NetDemoResponse;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaBytesInboundResponse extends NetKafkaInbound<String, byte[], NetDemoResponse> {

	@Override
	public boolean filter(NetKafkaIO<String, byte[]> io) {
		return false;
	}

	@Override
	public NetDemoResponse read(NetKafkaIO<String, byte[]> io) {
		return null;
	}

}
