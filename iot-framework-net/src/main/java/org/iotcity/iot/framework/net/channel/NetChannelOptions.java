package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.net.config.NetConfigInbound;
import org.iotcity.iot.framework.net.config.NetConfigOutbound;

/**
 * The network channel configuration options data.
 * @author ardon
 * @date 2021-07-09
 */
public class NetChannelOptions {

	/**
	 * The network channel inbound configuration data array (null by default).
	 */
	public NetConfigInbound[] inbounds;
	/**
	 * The network channel outbound configuration data array (null by default).
	 */
	public NetConfigOutbound[] outbounds;
	/**
	 * Indicates whether to use multithreading to process request and response data when allowed (false by default).
	 */
	public boolean multithreading = NetChannelHandler.CONST_MULTITHREADING;
	/**
	 * The thread execution priority of channel data processing (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	public int multithreadingPriority = NetChannelHandler.CONST_MULTITHREADING_PRIORITY;
	/**
	 * The default timeout value in milliseconds that waiting for a response data callback (120000 ms by default).
	 */
	public long defaultCallbackTimeout = NetChannelHandler.CONST_CALLBACK_TIMEOUT;
	/**
	 * If no data is received within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	public long receivingIdleTimeout = NetChannelHandler.CONST_RECEIVING_IDLE_TIMEOUT;
	/**
	 * If no data is sent within the specified idle time in milliseconds, the channel will be closed (0 by default, when it is set to 0, this option is disabled).
	 */
	public long sendingIdleTimeout = NetChannelHandler.CONST_SENDING_IDLE_TIMEOUT;

}
