package org.iotcity.iot.framework.net.channel;

/**
 * The network channel selector for channel selecting.
 * @author ardon
 * @date 2021-06-20
 */
public interface NetChannelSelector {

	/**
	 * Gets the channels in this selector.
	 */
	NetChannel[] getChannels();

}
