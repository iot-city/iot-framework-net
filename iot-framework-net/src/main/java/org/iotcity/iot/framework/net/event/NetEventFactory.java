package org.iotcity.iot.framework.net.event;

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
	NetChannelEvent createChannelEvent(Object source, NetChannel channel, NetChannelState state) throws IllegalArgumentException;

	/**
	 * Create a service event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param service The network service object (required, not null).
	 * @param state The network service state (required, not null).
	 * @return The network service event object for service state changing.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	NetServiceEvent createServiceEvent(Object source, NetService service, NetServiceState state) throws IllegalArgumentException;

	/**
	 * Create a session event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param session The network session object.
	 * @param state The network session state.
	 * @return The network session event object for session state changing.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	NetSessionEvent createSessionEvent(Object source, NetSession session, NetSessionState state) throws IllegalArgumentException;

	/**
	 * Create a network message request event (this event can not be cancelled).
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param io The network I/O object (required, not null).
	 * @param request The network request data object (required, not null).
	 * @param callback Network communication response callback object (required, not null).
	 * @return The network message request event object.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	NetMessageEvent createMessageEvent(Object source, NetIO<?, ?> io, NetData request, NetMessageEventCallback callback) throws IllegalArgumentException;

	/**
	 * Create a network message processing error event (this event can not be cancelled).
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param direction The network message data transmission direction (required, not null).
	 * @param messageIO The message I/O object (required, not null).
	 * @param requestData The request data object (set it to null when there is no request data).
	 * @param responseData The response data object (set it to null when there is no response data).
	 * @param exceptions Array of exception objects encountered when processing inbound or outbound data (set it to null when there is no exception).
	 * @param errorStatus The message processing error status (required, not null).
	 * @return The network message processing error event.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	NetMessageErrorEvent createMessageErrorEvent(Object source, NetMessageDirection direction, NetIO<?, ?> messageIO, NetData requestData, NetData responseData, Exception[] exceptions, NetMessageStatus errorStatus) throws IllegalArgumentException;

}
