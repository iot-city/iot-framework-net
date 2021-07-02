package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetInboundObject;
import org.iotcity.iot.framework.net.channel.NetOutboundObject;
import org.iotcity.iot.framework.net.channel.NetService;

/**
 * The network input and output handler.
 * @param <READER> Reader class type.
 * @param <SENDER> Sender class type.
 * @author ardon
 * @date 2021-06-18
 */
public class NetIOHandler<READER, SENDER> implements NetIO<READER, SENDER> {

	/**
	 * The network service of this I/O object (not null).
	 */
	private final NetService service;
	/**
	 * The network channel of this I/O object (not null).
	 */
	private final NetChannel channel;
	/**
	 * Whether the asynchronous processing mode is used.
	 */
	private final boolean asynchronous;
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
	 * @param channel The network channel object (required, not null).
	 * @param reader The message reader object.
	 * @param sender The message sender object.
	 * @param asynchronous Whether the asynchronous processing mode is used.
	 */
	public NetIOHandler(NetChannel channel, READER reader, SENDER sender, boolean asynchronous) {
		this.service = channel.getService();
		this.channel = channel;
		this.reader = reader;
		this.sender = sender;
		this.asynchronous = asynchronous;
	}

	@Override
	public NetService getService() {
		return service;
	}

	@Override
	public NetChannel getChannel() {
		return channel;
	}

	@Override
	public boolean isAsynchronous() {
		return asynchronous;
	}

	@Override
	public boolean isMultithreading() {
		return service.isMultithreading();
	}

	@Override
	public NetInboundObject[] getInbounds() {
		return service.getInbounds(getClass());
	}

	@Override
	public NetOutboundObject[] getOutbounds() {
		return service.getOutbounds(getClass());
	}

	@Override
	public NetResponser getResponser() {
		return channel.getResponser();
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
