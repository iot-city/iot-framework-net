package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.core.bus.BusDataListener;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.kafka.data.ClassB;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.bus.NetKafkaMessageEvent;
import org.iotcity.iot.framework.net.support.actor.NetActorResult;

/**
 * @author ardon
 * @date 2021-09-13
 */
@BusDataListener(value = NetKafkaActorRequest.class)
public class NetKafkaActorMessageListener implements BusEventListener {

	@Override
	public boolean onEvent(BusEvent event) throws Exception {
		NetKafkaActorRequest req = event.getEventData();
		if (req.callback != null) {
			ClassB b = (ClassB) req.params[1];
			NetKafkaMessageEvent ev = (NetKafkaMessageEvent) event;
			NetActorResult result = new NetActorResult(0, "msg", "refs");
			ev.sendResponse(NetMessageStatus.OK, new NetKafkaActorResponse(req.getMessageID(), req.callback, result, b));
		}
		return true;
	}

}
