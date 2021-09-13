package org.iotcity.iot.framework.net.kafka.support.bus;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.channel.NetServiceState;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetMessageDirection;
import org.iotcity.iot.framework.net.io.NetMessageStatus;
import org.iotcity.iot.framework.net.session.NetSession;
import org.iotcity.iot.framework.net.session.NetSessionState;
import org.iotcity.iot.framework.net.support.bus.NetChannelEvent;
import org.iotcity.iot.framework.net.support.bus.NetChannelEventData;
import org.iotcity.iot.framework.net.support.bus.NetEventFactory;
import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEventData;
import org.iotcity.iot.framework.net.support.bus.NetMessageEvent;
import org.iotcity.iot.framework.net.support.bus.NetMessageEventCallback;
import org.iotcity.iot.framework.net.support.bus.NetServiceEvent;
import org.iotcity.iot.framework.net.support.bus.NetServiceEventData;
import org.iotcity.iot.framework.net.support.bus.NetSessionEvent;
import org.iotcity.iot.framework.net.support.bus.NetSessionEventData;

/**
 * The kafka event factory for event creation.
 * @author ardon
 * @date 2021-06-22
 */
public final class NetKafkaEventFactory implements NetEventFactory {

	@Override
	public NetChannelEvent createChannelEvent(Object source, NetChannel channel, NetChannelState state) {
		return new NetKafkaChannelEvent(source, new NetChannelEventData(channel, state));
	}

	@Override
	public NetServiceEvent createServiceEvent(Object source, NetService service, NetServiceState state) {
		return new NetKafkaServiceEvent(source, new NetServiceEventData(service, state));
	}

	@Override
	public NetSessionEvent createSessionEvent(Object source, NetSession session, NetSessionState state) {
		return new NetKafkaSessionEvent(source, new NetSessionEventData(session, state));
	}

	@Override
	public NetMessageEvent createMessageEvent(Object source, NetIO<?, ?> io, NetData request, NetMessageEventCallback callback) throws IllegalArgumentException {
		return new NetKafkaMessageEvent(source, io, request, callback);
	}

	@Override
	public NetMessageErrorEvent createMessageErrorEvent(Object source, NetMessageDirection direction, NetIO<?, ?> messageIO, NetData requestData, NetData responseData, Exception[] exceptions, NetMessageStatus errorStatus) throws IllegalArgumentException {
		return new NetKafkaMessageErrorEvent(source, new NetMessageErrorEventData(direction, messageIO, requestData, responseData, exceptions, errorStatus));
	}

}
