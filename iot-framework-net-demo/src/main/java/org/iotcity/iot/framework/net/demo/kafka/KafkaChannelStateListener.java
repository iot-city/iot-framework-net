package org.iotcity.iot.framework.net.demo.kafka;

import org.iotcity.iot.framework.core.bus.BusDataListener;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;
import org.iotcity.iot.framework.net.kafka.support.bus.NetKafkaChannelEvent;
import org.iotcity.iot.framework.net.support.bus.NetChannelEventData;

/**
 * @author ardon
 * @date 2021-06-21
 */
@BusDataListener(value = NetChannelEventData.class, filterEvent = NetKafkaChannelEvent.class)
public class KafkaChannelStateListener implements BusEventListener {

	@Override
	public boolean onEvent(BusEvent event) throws Exception {
		return false;
	}

}
