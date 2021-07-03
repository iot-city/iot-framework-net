package org.iotcity.iot.framework.net.channel;

/**
 * The network service global configuration option data.
 * @author ardon
 * @date 2021-06-21
 */
public class NetServiceOptions {

	/**
	 * The thread execution priority of the current network service (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	public int multithreadingPriority = NetServiceHandler.CONST_MULTITHREADING_PRIORITY;

	/**
	 * The service monitoring interval in milliseconds for channel status check (10000 ms by default).
	 */
	public long serviceMonitoringInterval = NetServiceHandler.CONST_MONITORING_INTERVAL;

	/**
	 * The default timeout value in milliseconds that waiting for a response data callback (120000 ms by default).
	 */
	public long defaultCallbackTimeout = NetServiceHandler.CONST_CALLBACK_TIMEOUT;

	/**
	 * If no data is received within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	public long receivingIdleTimeout = NetServiceHandler.CONST_RECEIVING_IDLE_TIMEOUT;

	/**
	 * If no data is sent within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	public long sendingIdleTimeout = NetServiceHandler.CONST_SENDING_IDLE_TIMEOUT;

}
