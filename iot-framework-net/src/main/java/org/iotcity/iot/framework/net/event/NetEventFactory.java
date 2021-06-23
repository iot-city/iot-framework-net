package org.iotcity.iot.framework.net.event;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.channel.NetService;
import org.iotcity.iot.framework.net.channel.NetServiceState;
import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetDataResponse;
import org.iotcity.iot.framework.net.io.NetIO;
import org.iotcity.iot.framework.net.io.NetResponseCallback;
import org.iotcity.iot.framework.net.session.NetSession;
import org.iotcity.iot.framework.net.session.NetSessionState;

/**
 * The network event factory for event creation.
 * @author ardon
 * @date 2021-06-22
 */
public interface NetEventFactory {

	/**
	 * Create a channel event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param channel The network channel object (required, not null).
	 * @param state The network channel state (required, not null).
	 * @return The network channel event object for channel state changing.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	NetChannelEvent createChannelEvent(Object source, NetChannel channel, NetChannelState state);

	/**
	 * Create a service event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param service The network service object (required, not null).
	 * @param state The network service state (required, not null).
	 * @return The network service event object for service state changing.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	NetServiceEvent createServiceEvent(Object source, NetService service, NetServiceState state);

	/**
	 * Create a session event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param session The network session object.
	 * @param state The network session state.
	 * @return The network session event object for session state changing.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	NetSessionEvent createSessionEvent(Object source, NetSession session, NetSessionState state);

	/**
	 * Create a network asynchronous request data event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @param callbacker Network communication response callback object (required, not null).
	 * @return The network asynchronous request data event object.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	NetMessageAsyncEvent createMessageAsyncEvent(Object source, NetIO<?, ?, ?> io, NetData request, NetResponseCallback<NetDataResponse> callbacker);

	/**
	 * Create a network synchronous request data event.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @return The network synchronous request data event object.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	NetMessageSyncEvent createMessageSyncEvent(Object source, NetIO<?, ?, ?> io, NetData request);

}
