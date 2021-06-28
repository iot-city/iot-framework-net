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
	 * No response data.
	 */
	NO_RESPONSE,

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
	 * Error response result.
	 */
	RESPONSE_ERROR,

	/**
	 * The data callback method was executed multiple times.
	 */
	DUPLICATED,

	/**
	 * The data format is not compatible with the definition.
	 */
	INCOMPATIBLE,

	/**
	 * Waiting for data response timeout.
	 */
	TIMEOUT,

}
