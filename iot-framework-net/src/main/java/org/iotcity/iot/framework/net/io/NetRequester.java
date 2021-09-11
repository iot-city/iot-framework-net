package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetThreadLocal;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetInboundObject;
import org.iotcity.iot.framework.net.channel.NetOutboundObject;
import org.iotcity.iot.framework.net.support.bus.NetEventFactory;
import org.iotcity.iot.framework.net.support.bus.NetMessageErrorEvent;

/**
 * Network data communication request object.
 * @author ardon
 * @date 2021-06-20
 */
public final class NetRequester {

	// ---------------------------------------- ASYNCHRONOUS REQUEST METHODS ----------------------------------------

	/**
	 * Send request data using asynchronous response mode (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param io The network input and output object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The message process status.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "io", "request" or "responseClass" is null.
	 */
	public final <REQ extends NetDataRequest, RES extends NetDataResponse> NetMessageStatus asyncRequest(NetIO<?, ?> io, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) throws IllegalArgumentException {
		if (io == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter io, request and responseClass can not be null!");

		// Set request to current thread.
		NetThreadLocal.setCurrentrequest(request);
		// Get request data class.
		Class<?> requestClass = request.getClass();
		// Gets the channel of I/O object.
		NetChannel channel = io.getChannel();

		// Check channel and service state.
		if (channel.isClosed() || io.getService().isStopped()) {
			// Close the channel.
			try {
				channel.close();
				if (callback != null) {
					callback.callbackResult(new NetResponseResult<RES>(io, NetMessageStatus.CHANNEL_CLOSED, null));
				}
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
			// Return the close state.
			return NetMessageStatus.CHANNEL_CLOSED;
		} else if (io.getSender() == null) {
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.sender", io.getClass().getName(), requestClass.getName()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, null, NetMessageStatus.NO_SENDER);
			// Return no sender result.
			return NetMessageStatus.NO_SENDER;
		}

		// Fix timeout value.
		timeout = channel.fixCallbackTimeout(timeout);

		// Get outbounds.
		NetOutboundObject[] outbounds = io.getOutbounds();
		// Check outbounds.
		if (outbounds != null && outbounds.length > 0) {
			// Traverse outbounds.
			for (NetOutboundObject out : outbounds) {

				// Get outbound object.
				NetOutbound<?, ?> outbound = out.outbound;
				// Determine whether the request data type is extended from the outbound data type.
				if (!outbound.getDataClass().isAssignableFrom(requestClass)) continue;

				try {
					// Filter I/O and network data.
					if (!outbound.filterIO(io, request)) continue;
				} catch (Exception e) {

					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.send.err", outbound.getClass().getName(), requestClass.getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, new Exception[] {
						e
					}, NetMessageStatus.SEND_EXCEPTION);
					// Return the exception state of execution process.
					return NetMessageStatus.SEND_EXCEPTION;

				}

				// Check asynchronous mode.
				if (io.isAsynchronous()) {
					// Send request by using asynchronous mode.
					return asyncRequestWithCallback(outbound, io, request, responseClass, callback, timeout);
				} else {
					// Send request by using synchronous mode.
					return syncRequestWithCallback(outbound, io, request, responseClass, callback, timeout);
				}

			}
		}

		// Log error message.
		FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.outbound", io.getClass().getName(), requestClass.getName()));
		// Publish an error event.
		publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, null, NetMessageStatus.NO_OUTBOUND);
		// Return no outbound object by default.
		return NetMessageStatus.NO_OUTBOUND;

	}

	/**
	 * Send asynchronous request to remote end (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param outbound Network outbound data processing object (required, can not be null).
	 * @param io The network input and output object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The message process status.
	 */
	private final <REQ extends NetDataRequest, RES extends NetDataResponse> NetMessageStatus asyncRequestWithCallback(NetOutbound<?, ?> outbound, NetIO<?, ?> io, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) throws IllegalArgumentException {
		// Check asynchronous interface.
		if (!(request instanceof NetDataAsyncRequest)) {

			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.async.req.err", request.getClass().getName(), NetDataAsyncRequest.class.getName()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, null, NetMessageStatus.INCOMPATIBLE);
			// Return message status.
			return NetMessageStatus.INCOMPATIBLE;

		}

		// Get message ID.
		String queue = ((NetDataAsyncRequest) request).getMessageID();
		// Add callback to responser.
		if (callback != null) io.getResponser().addCallback(io, queue, request, responseClass, callback, timeout);

		try {

			// Send request to remote end.
			NetMessageStatus status = outbound.sendIO(io, request, timeout);
			// Update sent time.
			io.getChannel().updateSentTime();
			// Return sent status.
			return status == null ? NetMessageStatus.INCOMPATIBLE : status;

		} catch (Exception e) {

			// Log error message.
			FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.send.err", outbound.getClass().getName(), request.getClass().getName(), e.getMessage()), e);
			// Get exception status.
			NetMessageStatus status = NetMessageStatus.SEND_EXCEPTION;
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, new Exception[] {
				e
			}, status);

			// Callback using responser.
			if (callback != null) io.getResponser().tryCallback(NetMessageDirection.TO_REMOTE_REQUEST, io, queue, responseClass, status, null);

			// Return the exception state of execution process.
			return status;

		}
	}

	/**
	 * Send synchronous request to remote end (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param outbound Network outbound data processing object (required, can not be null).
	 * @param io The network input and output object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback Asynchronous response data callback processing object (optional, set it to null when callback response is not required).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The message process status.
	 */
	private final <REQ extends NetDataRequest, RES extends NetDataResponse> NetMessageStatus syncRequestWithCallback(NetOutbound<?, ?> outbound, NetIO<?, ?> io, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) throws IllegalArgumentException {
		// Define message status.
		NetMessageStatus status;

		// ------------------------ Send request to remote end ------------------------

		try {

			// Send request to remote end.
			status = outbound.sendIO(io, request, timeout);
			// Update sent time.
			io.getChannel().updateSentTime();

		} catch (Exception e) {

			// Log error message.
			FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.send.err", outbound.getClass().getName(), request.getClass().getName(), e.getMessage()), e);
			// Get exception status.
			status = NetMessageStatus.SEND_EXCEPTION;
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, new Exception[] {
				e
			}, status);

			// Handle response callback.
			if (callback != null) {

				try {
					// Callback response result.
					callback.callbackResult(new NetResponseResult<RES>(io, status, null));
				} catch (Exception e2) {
					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.logic.res.err", responseClass.getName(), e2.getMessage()), e2);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, new Exception[] {
						e2
					}, NetMessageStatus.CALLBACK_EXCEPTION);
				}

			}

			// Return the exception state of execution process.
			return status;
		}

		// ------------------------ Read response from remote end ------------------------

		// Check message status.
		if (status == NetMessageStatus.OK || status == NetMessageStatus.ACCEPTED) {

			// Read response result form I/O object.
			NetResponseResult<RES> result = readSyncResponse(io, request, responseClass);

			// Handle response callback.
			if (callback != null) {

				try {
					// Callback response result.
					callback.callbackResult(result);
				} catch (Exception e) {
					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.logic.res.err", responseClass.getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.FROM_REMOTE_RESPONSE, io, request, result.getResponse(), new Exception[] {
						e
					}, NetMessageStatus.CALLBACK_EXCEPTION);
				}

			}

			// Return result status.
			return result.getStatus();
		} else {
			// Return error message status.
			return status == null ? NetMessageStatus.INCOMPATIBLE : status;
		}
	}

	/**
	 * Read synchronous response from remote end (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param io The network input and output object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @return The network response result data.
	 */
	private final <REQ extends NetDataRequest, RES extends NetDataResponse> NetResponseResult<RES> readSyncResponse(NetIO<?, ?> io, REQ request, Class<RES> responseClass) throws IllegalArgumentException {

		// Check reader object.
		if (io.getReader() == null) {
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.reader", io.getClass().getName()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.FROM_REMOTE_RESPONSE, io, request, null, null, NetMessageStatus.NO_READER);
			// Return no reader result.
			return new NetResponseResult<RES>(io, NetMessageStatus.NO_READER, null);
		}

		// Get inbounds.
		NetInboundObject[] inbounds = io.getInbounds();
		// Check inbounds.
		if (inbounds != null && inbounds.length > 0) {
			// Traverse inbounds.
			for (NetInboundObject in : inbounds) {

				// Get inbound object.
				NetInbound<?, ?> inbound = in.inbound;
				// Determine whether the response data type is extended from the inbound data type.
				if (!inbound.getDataClass().isAssignableFrom(responseClass)) continue;

				try {

					// Filter I/O object.
					if (!inbound.filterIO(io)) continue;
					// Read network data from I/O object.
					@SuppressWarnings("unchecked")
					RES response = (RES) inbound.readIO(io);
					// Update message time.
					if (response != null) io.getChannel().updateMessageTime();
					// Return response result.
					return new NetResponseResult<RES>(io, NetMessageStatus.OK, response);

				} catch (Exception e) {

					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.read.err", inbound.getClass().getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.FROM_REMOTE_RESPONSE, io, request, null, new Exception[] {
						e
					}, NetMessageStatus.READ_EXCEPTION);
					// Return the exception state of execution process.
					return new NetResponseResult<RES>(io, NetMessageStatus.READ_EXCEPTION, null);

				}

			}
		}

		// Log error message.
		FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.inbound.spec", io.getClass().getName(), responseClass.getName()));
		// Publish an error event.
		publishErrorEvent(NetMessageDirection.FROM_REMOTE_RESPONSE, io, request, null, null, NetMessageStatus.NO_INBOUND);
		// Return no inbound by default.
		return new NetResponseResult<RES>(io, NetMessageStatus.NO_INBOUND, null);
	}

	// ---------------------------------------- SYNCHRONOUS REQUEST METHODS ----------------------------------------

	/**
	 * Send request data using synchronous response mode (returns not null).
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param io The network input and output object (required, can not be null).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (optional, the global configuration timeout is used when the parameter value is less than or equal to 0).
	 * @return The network response result data.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "io", "request" or "responseClass" is null.
	 */
	public final <REQ extends NetDataRequest, RES extends NetDataResponse> NetResponseResult<RES> syncRequest(NetIO<?, ?> io, REQ request, Class<RES> responseClass, long timeout) throws IllegalArgumentException {
		if (io == null || request == null || responseClass == null) throw new IllegalArgumentException("Parameter io, request and responseClass can not be null!");

		// Set request to current thread.
		NetThreadLocal.setCurrentrequest(request);
		// Get request data class.
		Class<?> requestClass = request.getClass();
		// Gets the channel of I/O object.
		NetChannel channel = io.getChannel();

		// Check channel and service state.
		if (channel.isClosed() || io.getService().isStopped()) {
			// Close the channel.
			try {
				channel.close();
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
			// Return the close state.
			return new NetResponseResult<RES>(io, NetMessageStatus.CHANNEL_CLOSED, null);
		} else if (io.getSender() == null) {
			// Log error message.
			FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.sender", io.getClass().getName(), requestClass.getName()));
			// Publish an error event.
			publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, null, NetMessageStatus.NO_SENDER);
			// Return no sender result.
			return new NetResponseResult<RES>(io, NetMessageStatus.NO_SENDER, null);
		}

		// Fix timeout value.
		timeout = channel.fixCallbackTimeout(timeout);

		// Get outbounds.
		NetOutboundObject[] outbounds = io.getOutbounds();
		// Check outbounds.
		if (outbounds != null && outbounds.length > 0) {
			// Traverse outbounds.
			for (NetOutboundObject out : outbounds) {

				// Get outbound object.
				NetOutbound<?, ?> outbound = out.outbound;
				// Determine whether the request data type is extended from the outbound data type.
				if (!outbound.getDataClass().isAssignableFrom(requestClass)) continue;

				try {
					// Filter I/O and network data.
					if (!outbound.filterIO(io, request)) continue;
				} catch (Exception e) {

					// Log error message.
					FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.send.err", outbound.getClass().getName(), requestClass.getName(), e.getMessage()), e);
					// Publish an error event.
					publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, new Exception[] {
						e
					}, NetMessageStatus.SEND_EXCEPTION);
					// Return the exception state of execution process.
					return new NetResponseResult<RES>(io, NetMessageStatus.SEND_EXCEPTION, null);

				}

				// Create response callback locker.
				NetResponseCallbackLocker<RES> callback = new NetResponseCallbackLocker<>();
				// Check asynchronous mode.
				if (io.isAsynchronous()) {

					// Send request by using asynchronous mode.
					NetMessageStatus status = asyncRequestWithCallback(outbound, io, request, responseClass, callback, timeout);
					// Check send status.
					if (status == NetMessageStatus.ACCEPTED || status == NetMessageStatus.OK) {
						// Waiting for asynchronous response.
						callback.waitForResponse(io, timeout);
						// Get response result.
						NetResponseResult<RES> result = callback.getResult();
						// Result result.
						return result == null ? new NetResponseResult<RES>(io, NetMessageStatus.WRONG_RESPONSE_MODE, null) : result;
					} else {
						// Returns the failed result.
						return new NetResponseResult<RES>(io, status, null);
					}

				} else {

					// Send request by using synchronous mode.
					NetMessageStatus status = syncRequestWithCallback(outbound, io, request, responseClass, callback, timeout);
					// Get response result.
					NetResponseResult<RES> result = callback.getResult();
					// Result result.
					return result == null ? new NetResponseResult<RES>(io, status, null) : result;

				}

			}
		}

		// Log error message.
		FrameworkNet.getLogger().warn(FrameworkNet.getLocale().text("net.message.no.outbound", io.getClass().getName(), requestClass.getName()));
		// Publish an error event.
		publishErrorEvent(NetMessageDirection.TO_REMOTE_REQUEST, io, request, null, null, NetMessageStatus.NO_OUTBOUND);
		// Return no outbound object by default.
		return new NetResponseResult<RES>(io, NetMessageStatus.NO_OUTBOUND, null);
	}

	// ---------------------------------------- PRIVATE COMMON METHODS ----------------------------------------

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
	private final void publishErrorEvent(NetMessageDirection direction, NetIO<?, ?> messageIO, NetData requestData, NetData responseData, Exception[] exceptions, NetMessageStatus errorStatus) throws IllegalArgumentException {
		// Get event factory and publisher.
		NetEventFactory factory = messageIO.getService().getEventFactory();
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		// Create an error event.
		NetMessageErrorEvent event = factory.createMessageErrorEvent(this, direction, messageIO, requestData, responseData, exceptions, errorStatus);
		// Publish the error event.
		publisher.publish(event);
	}

}
