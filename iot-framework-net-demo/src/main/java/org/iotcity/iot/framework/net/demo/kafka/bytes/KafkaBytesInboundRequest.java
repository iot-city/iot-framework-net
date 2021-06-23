package org.iotcity.iot.framework.net.demo.kafka.bytes;

import org.iotcity.iot.framework.net.demo.NetDemoRequest;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaBytesInboundRequest extends NetKafkaInbound<String, byte[], NetDemoRequest> {

	@Override
	public boolean filter(NetKafkaIO<String, byte[]> io) {
		return false;
	}

	@Override
	public NetDemoRequest read(NetKafkaIO<String, byte[]> io) {
		return null;
	}

}
