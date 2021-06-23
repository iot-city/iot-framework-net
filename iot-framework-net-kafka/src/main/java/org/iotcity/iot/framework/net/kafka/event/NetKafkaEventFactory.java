package org.iotcity.iot.framework.net.kafka.event;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.channel.NetServiceState;
import org.iotcity.iot.framework.net.event.NetChannelEvent;
import org.iotcity.iot.framework.net.event.NetChannelEventData;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.event.NetMessageAsyncEvent;
import org.iotcity.iot.framework.net.event.NetMessageSyncEvent;
import org.iotcity.iot.framework.net.event.NetServiceEvent;
import org.iotcity.iot.framework.net.event.NetServiceEventData;
import org.iotcity.iot.framework.net.event.NetSessionEvent;
import org.iotcity.iot.framework.net.event.NetSessionEventData;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetResponseCallback;
import org.iotcity.iot.framework.net.session.NetSession;
import org.iotcity.iot.framework.net.session.NetSessionState;

/**
 * @author ardon
 * @date 2021-06-22
 */
public class NetKafkaEventFactory implements NetEventFactory {

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
	public NetMessageAsyncEvent createMessageAsyncEvent(Object source, NetIO<?, ?, ?> io, NetData request, NetResponseCallback<NetDataResponse> callbacker) {
		return new NetKafkaMessageAsyncEvent(source, io, request, callbacker);
	}

	@Override
	public NetMessageSyncEvent createMessageSyncEvent(Object source, NetIO<?, ?, ?> io, NetData request) {
		return new NetKafkaMessageSyncEvent(source, io, request);
	}

}
