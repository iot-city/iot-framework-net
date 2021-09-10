package org.iotcity.iot.framework.net.kafka.support.actor.io;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.io.NetOutboundHandler;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaIOString;

/**
 * @author ardon
 * @date 2021-09-10
 */
public final class NetKafkaActorStringOutbound extends NetOutboundHandler<NetKafkaIOString, NetData> {

	@Override
	public boolean filter(NetKafkaIOString io, NetData data) {
		return false;
	}

	@Override
	public NetMessageStatus send(NetKafkaIOString io, NetData data, long timeout) throws Exception {
		return null;
	}

}
