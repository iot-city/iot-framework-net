package org.iotcity.iot.framework.net.channel;

/**
 * Enumeration of state definitions for network services.
 * @author ardon
 * @date 2021-06-22
 */
public enum NetServiceState {

	/**
	 * The network service is created and ready to start.
	 */
	CREATED,
	/**
	 * The network service is starting (for publishing events only, not the real state of the service. this event can be cancelled).
	 */
	STARTING,
	/**
	 * The network service is started.
	 */
	STARTED,
	/**
	 * The network service is stopping (for publishing events only, not the real state of the service. this event can be cancelled).
	 */
	STOPPING,
	/**
	 * The network service is stopped.
	 */
	STOPPED,

}
