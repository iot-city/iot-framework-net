package org.iotcity.iot.framework.net.channel;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.config.NetConfigService;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.io.NetInbound;
import org.iotcity.iot.framework.net.io.NetOutbound;

/**
 * The network service.
 * @author ardon
 * @date 2021-06-19
 */
public interface NetService extends Configurable<NetConfigService> {

	/**
	 * Gets the network manager (returns not null).
	 */
	NetManager getNetManager();

	/**
	 * Gets the service unique identification (returns not null).
	 */
	String getServiceID();

	/**
	 * Gets the service monitoring interval in milliseconds for channel status check.
	 */
	long getMonitoringInterval();

	/**
	 * Gets the current state of this service.
	 */
	NetServiceState getState();

	/**
	 * Indicates whether this service has been started.
	 */
	boolean isStarted();

	/**
	 * Indicates whether this service has been stopped.
	 */
	boolean isStopped();

	/**
	 * Gets the create time of this service in milliseconds.
	 */
	long getCreateTime();

	/**
	 * Gets the start time of this service in milliseconds (returns 0 when not started).
	 */
	long getStartTime();

	/**
	 * Gets the stop time of this service in milliseconds (returns 0 when not stopped).
	 */
	long getStopTime();

	/**
	 * Gets the last time in milliseconds that this service receives a message (returns 0 when no message is received).
	 */
	long getMessageTime();

	/**
	 * Gets the time in milliseconds that this service last sent a message (returns 0 when no message is sent).
	 */
	long getSentTime();

	/**
	 * Gets a channel by a specified channel ID (returns null if the channel does not exists in this service).
	 * @param channelID The channel unique identification.
	 * @return A network channel object or null.
	 */
	NetChannel getChannel(String channelID);

	/**
	 * Filter and get channel objects (returns not null).
	 * @param filter The channel filter object.
	 * @return Array of channel objects that meet the filtering conditions.
	 */
	NetChannel[] filterChannels(NetChannelFilter filter);

	/**
	 * Gets all channels in this service (returns not null).
	 */
	NetChannel[] getChannels();

	/**
	 * Gets the channel size in this service.
	 */
	int getChannelSize();

	/**
	 * Add inbound message processing object to this service.
	 * @param inbound Network inbound message processing object.
	 * @param priority Inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	void addInbound(NetInbound<?, ?> inbound, int priority);

	/**
	 * Remove inbound message processing object from this service.
	 * @param inbound Network inbound message processing object.
	 */
	void removeInbound(NetInbound<?, ?> inbound);

	/**
	 * Gets inbound message processing objects from this service (returns null if there is no inbound for current I/O object).
	 * @param netIOClass The network I/O object class (extended from: {@link org.iotcity.iot.framework.net.io.NetIO }).
	 * @return Inbound message processing objects.
	 */
	NetInboundObject[] getInbounds(Class<?> netIOClass);

	/**
	 * Remove all inbound message processing objects from this service.
	 */
	void clearInbounds();

	/**
	 * Add outbound message processing object to this service.
	 * @param outbound Network outbound message processing object.
	 * @param priority Outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	void addOutbound(NetOutbound<?, ?> outbound, int priority);

	/**
	 * Remove outbound message processing object from this service.
	 * @param outbound Network outbound message processing object.
	 */
	void removeOutbound(NetOutbound<?, ?> outbound);

	/**
	 * Gets outbound message processing objects from this service (returns null if there is no outbound for current I/O object).
	 * @param netIOClass The network I/O object class (extended from: {@link org.iotcity.iot.framework.net.io.NetIO }).
	 * @return Outbound message processing objects.
	 */
	NetOutboundObject[] getOutbounds(Class<?> netIOClass);

	/**
	 * Remove all outbound message processing objects from this service.
	 */
	void clearOutbounds();

	/**
	 * Start this service.
	 * @return Returns whether this service was successfully started.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean start() throws Exception;

	/**
	 * Stop this service.
	 * @return Returns whether this service was successfully stopped.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	boolean stop() throws Exception;

	/**
	 * Gets the event factory to create net events (returns not null).
	 */
	NetEventFactory getEventFactory();

}
