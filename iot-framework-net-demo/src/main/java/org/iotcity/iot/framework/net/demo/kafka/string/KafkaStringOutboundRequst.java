package org.iotcity.iot.framework.net.demo.kafka.string;

import org.iotcity.iot.framework.net.demo.NetDemoRequest;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaOutbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaStringOutboundRequst extends NetKafkaOutbound<String, String, NetDemoRequest> {

	@Override
	public boolean filter(NetKafkaIO<String, String> io, NetDemoRequest data) {
		return false;
	}

	@Override
	public NetMessageStatus send(NetKafkaIO<String, String> io, NetDemoRequest data, long timeout) throws Exception {
		return null;
	}

}
