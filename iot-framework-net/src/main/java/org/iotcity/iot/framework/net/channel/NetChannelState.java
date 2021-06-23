package org.iotcity.iot.framework.net.channel;

/**
 * Enumeration of state definitions for network channels.
 * @author ardon
 * @date 2021-06-21
 */
public enum NetChannelState {

	/**
	 * The network channel is created and ready to open.
	 */
	CREATED,
	/**
	 * The network channel is opening (for publishing events only, not the real state of the channel. this event can be cancelled).
	 */
	OPENING,
	/**
	 * The network channel is opened.
	 */
	OPENED,
	/**
	 * The network channel is closing (for publishing events only, not the real state of the channel. this event can be cancelled).
	 */
	CLOSING,
	/**
	 * The network channel is closed.
	 */
	CLOSED,

}
