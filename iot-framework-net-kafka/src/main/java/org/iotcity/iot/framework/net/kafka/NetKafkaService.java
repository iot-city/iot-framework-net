package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.kafka.event.NetKafkaEventFactory;

/**
 * @author ardon
 * @date 2021-06-20
 */
public class NetKafkaService extends NetServiceHandler {

	private final NetEventFactory eventFactory = new NetKafkaEventFactory();

	public NetKafkaService(NetManager manager, String serviceID) throws IllegalArgumentException {
		super(manager, serviceID);
	}

	@Override
	public NetEventFactory getEventFactory() {
		return eventFactory;
	}

	@Override
	protected boolean doConfig(PropertiesConfigFile file) {
		return false;
	}

	@Override
	protected boolean doStart() throws Exception {
		return false;
	}

	@Override
	protected boolean doStop() throws Exception {
		return false;
	}

}
