package org.iotcity.iot.framework.net.demo.kafka.string;

import org.iotcity.iot.framework.net.demo.NetDemoResponse;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaStringInboundResponse extends NetKafkaInbound<String, String, NetDemoResponse> {

	@Override
	public boolean filter(NetKafkaIO<String, String> io) {
		return false;
	}

	@Override
	public NetDemoResponse read(NetKafkaIO<String, String> io) {
		return null;
	}

}
