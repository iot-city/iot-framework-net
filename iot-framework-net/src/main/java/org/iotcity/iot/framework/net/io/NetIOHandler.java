package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannel;

/**
 * The network input and output handler.
 * @param <CHANNEL> Network channel class type.
 * @param <READER> Reader class type.
 * @param <SENDER> Sender class type.
 * @author ardon
 * @date 2021-06-18
 */
public class NetIOHandler<CHANNEL extends NetChannel, READER, SENDER> implements NetIO<CHANNEL, READER, SENDER> {

	/**
	 * The network channel object.
	 */
	private final CHANNEL channel;
	/**
	 * The message reader object.
	 */
	private final READER reader;
	/**
	 * The message sender object.
	 */
	private final SENDER sender;

	/**
	 * Constructor for network input and output handler.
	 * @param channel The network channel object.
	 * @param reader The message reader object.
	 * @param sender The message sender object.
	 */
	public NetIOHandler(CHANNEL channel, READER reader, SENDER sender) {
		this.channel = channel;
		this.reader = reader;
		this.sender = sender;
	}

	@Override
	public CHANNEL getChannel() {
		return channel;
	}

	@Override
	public READER getReader() {
		return reader;
	}

	@Override
	public SENDER getSender() {
		return sender;
	}

}
