package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOString;

/**
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaActorStringInbound extends NetInboundHandler<NetKafkaIOString, NetData> {

	@Override
	public boolean filter(NetKafkaIOString io) {
		return false;
	}

	@Override
	public NetData read(NetKafkaIOString io) throws Exception {
		return null;
	}

}
