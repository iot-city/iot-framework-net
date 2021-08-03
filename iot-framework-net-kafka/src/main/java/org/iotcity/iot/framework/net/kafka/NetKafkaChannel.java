package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.channel.NetChannelHandler;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.io.NetIO;

/**
 * @author ardon
 * @date 2021-06-16
 */
public class NetKafkaChannel<K, V> extends NetChannelHandler {

	public NetKafkaChannel(NetServiceHandler service, String channelID) throws IllegalArgumentException {
		super(service, channelID);
	}

	@Override
	public NetIO<?, ?> getToRemoteIO() {
		return null;
	}

	@Override
	protected boolean doOpen() throws Exception {
		return false;
	}

	@Override
	protected boolean doClose() throws Exception {
		return false;
	}

}
