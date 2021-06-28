package org.iotcity.iot.framework.net.channel;

/**
 * The network channel filter.
 * @author ardon
 * @date 2021-06-21
 */
public interface NetChannelFilter {

	/**
	 * Test and output qualified channel objects.
	 * @param channel The channel object to meet conditions.
	 * @return Returns true when the filter conditions are met, otherwise, returns false.
	 */
	boolean test(NetChannel channel);

}
