package org.iotcity.iot.framework.net.io;

/**
 * Network message status definition enumeration.
 * @author ardon
 * @date 2021-06-16
 */
public enum NetMessageStatus {

	/**
	 * Message processing succeeded.
	 */
	OK,

	/**
	 * The message has been accepted but has not yet processed it (asynchronous processing).
	 */
	ACCEPTED,

	/**
	 * Business logic processing failed.
	 */
	LOGICAL_FAILED,

	/**
	 * The reader object in network I/O does not found.
	 */
	NO_READER,

	/**
	 * The sender object in network I/O does not found.
	 */
	NO_SENDER,

	/**
	 * Inbound processing object not found.
	 */
	NO_INBOUND,

	/**
	 * Outbound processing object not found.
	 */
	NO_OUTBOUND,

	/**
	 * There is no message handling event listener.
	 */
	NO_MESSAGE_LISTENER,

	/**
	 * Error reading inbound data.
	 */
	READ_EXCEPTION,

	/**
	 * Business logic processing exception.
	 */
	LOGICAL_EXCEPTION,

	/**
	 * Error reading outbound data.
	 */
	SEND_EXCEPTION,

	/**
	 * Exception encountered while executing callback processing.
	 */
	CALLBACK_EXCEPTION,

	/**
	 * Wrong response mode, in the life cycle of synchronous I/O request, the response result should be returned synchronously.
	 */
	WRONG_RESPONSE_MODE,

	/**
	 * Waiting for data response timeout.
	 */
	RESPONSE_TIMEOUT,

	/**
	 * The data callback method was executed multiple times.
	 */
	DUPLICATED,

	/**
	 * The data format is not compatible with the definition.
	 */
	INCOMPATIBLE,

	/**
	 * The network channel is being closed.
	 */
	CHANNEL_CLOSING,

	/**
	 * The network channel has been closed.
	 */
	CHANNEL_CLOSED,

}
