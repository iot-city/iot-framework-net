package org.iotcity.iot.framework.net.demo.kafka.string;

import org.iotcity.iot.framework.net.demo.NetDemoRequest;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaInbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaStringInboundRequst extends NetKafkaInbound<String, String, NetDemoRequest> {

	@Override
	public boolean filter(NetKafkaIO<String, String> io) {
		return false;
	}

	@Override
	public NetDemoRequest read(NetKafkaIO<String, String> io) {
		return null;
	}

}
