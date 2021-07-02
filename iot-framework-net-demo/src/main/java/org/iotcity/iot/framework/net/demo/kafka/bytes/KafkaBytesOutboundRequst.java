package org.iotcity.iot.framework.net.demo.kafka.bytes;

import org.iotcity.iot.framework.net.demo.NetDemoRequest;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.kafka.NetKafkaIO;
import org.iotcity.iot.framework.net.kafka.NetKafkaOutbound;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class KafkaBytesOutboundRequst extends NetKafkaOutbound<String, byte[], NetDemoRequest> {

	@Override
	public boolean filter(NetKafkaIO<String, byte[]> io, NetDemoRequest data) {
		return false;
	}

	@Override
	public NetMessageStatus send(NetKafkaIO<String, byte[]> io, NetDemoRequest data, long timeout) throws Exception {
		return null;
	}

}
