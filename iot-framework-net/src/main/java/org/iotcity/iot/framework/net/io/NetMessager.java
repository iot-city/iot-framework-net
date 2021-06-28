package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.channel.NetInboundObject;
import org.iotcity.iot.framework.net.channel.NetOutboundObject;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.event.NetMessageErrorEvent;
import org.iotcity.iot.framework.net.event.NetMessageEvent;
import org.iotcity.iot.framework.net.event.NetMessageEventCallback;

/**
 * Network message processing object used to process inbound data.
 * @author ardon
 * @date 2021-06-21
 */
public final class NetMessager {

	/**
	 * Use the network I/O object to read and process inbound data.
	 * @param io The network input and output object (required, can not be null).
	 * @return The message process status.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "io" is null.
	 */
	public NetMessageStatus onMessage(NetIO<?, ?> io) throws IllegalArgumentException {
		if (io == null) throw new IllegalArgumentException("Parameter io can not be null!");

		// Get inbounds.
		NetInboundObject[] inbounds = io.getInbounds();
		// Check inbounds.
		if (inbounds != null && inbounds.length > 0) {
			// Traverse inbounds.
			for (NetInboundObject in : inbounds) {

				// Define network data.
				NetData data = null;
				// Get inbound object.
				NetInbound<?, ?> inbound = in.inbound;
				try {

					// Filter I/O object.
					if (!inbound.filterIO(io)) continue;
					// Read network data from I/O object.
					data = inbound.readIO(io);

				} catch (Exception e) {

					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.read.err", inbound.getClass().getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.FROM_REMOTE_DATA, io, null, null, new Exception[] {
						e
					}, NetMessageStatus.READ_EXCEPTION);
					// Return the exception state of execution process.
					return NetMessageStatus.READ_EXCEPTION;

				}
				// Check for null value.
				if (data == null) continue;
				// Update message time.
				io.getChannel().updateMessageTime();

				// Determine the network data.
				if (data.isRequest()) {

					// Handle request data from the remote end.
					return handleRequest(io, data);

				} else {
					// Check response data type.
					if (data instanceof NetDataAsyncResponse) {

						// Get response data.
						NetDataAsyncResponse response = (NetDataAsyncResponse) data;
						// Process the asynchronous response data from the remote end.
						io.getResponser().tryCallback(NetMessageDirection.FROM_REMOTE_RESPONSE, io, response.getMessageQueue(), response.getClass(), NetMessageStatus.OK, response);
						// Return status.
						return NetMessageStatus.OK;

					} else {

						// Log error message.
						FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.async.res.err", data.getClass().getName(), NetDataAsyncResponse.class.getName()));
						// Publish an error event.
						publishErrorEvent(NetMessageDirection.FROM_REMOTE_RESPONSE, io, null, data, null, NetMessageStatus.INCOMPATIBLE);
						// Return incompatible status.
						return NetMessageStatus.INCOMPATIBLE;

					}
				}

			}
		}

		// Log error message.
		FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.inbound", io.getClass().getName()));
		// Publish an error event.
		publishErrorEvent(NetMessageDirection.FROM_REMOTE_DATA, io, null, null, null, NetMessageStatus.NO_INBOUND);
		// Return no inbound by default.
		return NetMessageStatus.NO_INBOUND;

	}

	/**
	 * Handle request data from the remote end.
	 * @param io The network input and output object.
	 * @param request The network request data object.
	 * @return The message process status.
	 */
	private NetMessageStatus handleRequest(NetIO<?, ?> io, NetData request) {

		// Get event factory and publisher.
		NetEventFactory factory = io.getService().getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();

		// Create a message event.
		NetMessageEvent event = factory.createMessageEvent(this, io, request, new NetMessageEventCallback() {

			@Override
			public NetMessageStatus onCallback(NetMessageEvent event, NetMessageStatus status, NetDataResponse response) {
				// Send response to remote end.
				return sendResponse(io, request, response);
			}

		});

		// Publish message event.
		publisher.publish(event);
		// Gets the event response data (it is null if the callback is not executed).
		NetDataResponse response = event.getBusinessResponse();
		// Check for exceptions.
		if (event.hasException()) {

			// Get exceptions.
			Exception[] exes = event.getExceptions();
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.logic.req.err", request.getClass().getName(), exes[0].getMessage()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.FROM_REMOTE_REQUEST, io, request, response, exes, NetMessageStatus.LOGICAL_EXCEPTION);
			// Return the exception state of execution process.
			return NetMessageStatus.LOGICAL_EXCEPTION;

		} else if (event.getExecutionCount() > 0) {

			// Determine the asynchronous request mode.
			if (io.isAsynchronous()) {

				// Check callback execution in asynchronous mode.
				if (event.hasCalledSending()) {
					// Return the sent status.
					return event.getSentStatus();
				} else {
					// Return request message was accepted.
					return NetMessageStatus.ACCEPTED;
				}

			} else {

				// Check callback execution in synchronous mode.
				if (event.hasCalledSending()) {
					// Return the callback status.
					return event.getSentStatus();
				} else {
					// Log error message.
					FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.sync.res.err", request.getClass().getName()));
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.TO_REMOTE_RESPONSE, io, request, response, null, NetMessageStatus.WRONG_RESPONSE_MODE);
					// Return response error status.
					return NetMessageStatus.WRONG_RESPONSE_MODE;
				}

			}

		} else {

			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.listener", request.getClass().getName()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.FROM_REMOTE_REQUEST, io, request, response, null, NetMessageStatus.NO_MESSAGE_LISTENER);
			// Return message listener not found.
			return NetMessageStatus.NO_MESSAGE_LISTENER;

		}

	}

	/**
	 * Send response message to remote end.
	 * @param io The network input and output object.
	 * @param request The network request data object.
	 * @param response The network response data object.
	 * @return The message process status.
	 */
	private NetMessageStatus sendResponse(NetIO<?, ?> io, NetData request, NetDataResponse response) {
		// Check for response null value.
		if (response == null) return NetMessageStatus.OK;
		// Get response data class.
		Class<?> responseClass = response.getClass();

		// Get outbounds.
		NetOutboundObject[] outbounds = io.getOutbounds();
		// Check outbounds.
		if (outbounds != null && outbounds.length > 0) {
			// Traverse outbounds.
			for (NetOutboundObject out : outbounds) {

				// Get outbound object.
				NetOutbound<?, ?> outbound = out.outbound;
				// Determine whether the response data type is extended from the outbound data type.
				if (!outbound.getDataClass().isAssignableFrom(responseClass)) continue;

				try {

					// Filter I/O and network data.
					if (!outbound.filterIO(io, response)) continue;
					// Send response to remote end.
					NetMessageStatus status = outbound.sendIO(io, response);
					// Update sent time.
					io.getChannel().updateSentTime();
					// Return sent status.
					return status;

				} catch (Exception e) {

					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.send.err", outbound.getClass().getName(), responseClass.getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.TO_REMOTE_RESPONSE, io, request, response, new Exception[] {
						e
					}, NetMessageStatus.SEND_EXCEPTION);
					// Return the exception state of execution process.
					return NetMessageStatus.SEND_EXCEPTION;

				}

			}
		}

		// Log error message.
		FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.outbound", io.getClass().getName(), responseClass.getName()));
		// Publish an error event.
		publishErrorEvent(NetMessageDirection.TO_REMOTE_RESPONSE, io, request, response, null, NetMessageStatus.NO_OUTBOUND);
		// Return no outbound object by default.
		return NetMessageStatus.NO_OUTBOUND;

	}

	/**
	 * Publish a network message processing error event (this event can not be cancelled).
	 * @param direction The network message data transmission direction (required, not null).
	 * @param messageIO The message I/O object (required, not null).
	 * @param requestData The request data object (set it to null when there is no request data).
	 * @param responseData The response data object (set it to null when there is no response data).
	 * @param exceptions Array of exception objects encountered when processing inbound or outbound data (set it to null when there is no exception).
	 * @param errorStatus The message processing error status (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "request" is null.
	 */
	private void publishErrorEvent(NetMessageDirection direction, NetIO<?, ?> messageIO, NetData requestData, NetData responseData, Exception[] exceptions, NetMessageStatus errorStatus) throws IllegalArgumentException {
		// Get event factory and publisher.
		NetEventFactory factory = messageIO.getService().getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		// Create an error event.
		NetMessageErrorEvent event = factory.createMessageErrorEvent(this, direction, messageIO, requestData, responseData, exceptions, errorStatus);
		// Publish the error event.
		publisher.publish(event);
	}

}
