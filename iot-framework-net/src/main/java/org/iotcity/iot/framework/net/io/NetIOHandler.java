package org.iotcity.iot.framework.net.io;

import org.iotcity.iot.framework.net.NetThreadLocal;
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
public class NetIOHandler<READER extends NetReader, SENDER extends NetSender> implements NetIO<READER, SENDER> {

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
	public final NetService getService() {
		return service;
	}

	@Override
	public final NetChannel getChannel() {
		return channel;
	}

	@Override
	public final boolean isAsynchronous() {
		return asynchronous;
	}

	@Override
	public final boolean isMultithreading() {
		return channel.isMultithreading();
	}

	@Override
	public final NetInboundObject[] getInbounds() {
		NetInboundObject[] inbounds = channel.getInbounds(getClass());
		return inbounds == null ? service.getInbounds(getClass()) : inbounds;
	}

	@Override
	public final NetOutboundObject[] getOutbounds() {
		NetOutboundObject[] outbounds = channel.getOutbounds(getClass());
		return outbounds == null ? service.getOutbounds(getClass()) : outbounds;
	}

	@Override
	public <REQ extends NetDataRequest, RES extends NetDataResponse> REQ getCallbackRequest(String messageID, Class<REQ> requestClass, Class<RES> responseClass) {
		return channel.getResponser().getRequest(messageID, requestClass, responseClass);
	}

	@Override
	public <REQ extends NetDataRequest> REQ getCurrentRequest() {
		@SuppressWarnings("unchecked")
		REQ req = (REQ) NetThreadLocal.getCurrentRequest();
		return req;
	}

	@Override
	public final NetResponser getResponser() {
		return channel.getResponser();
	}

	@Override
	public final <T> T getClassFactory() {
		return service.getClassFactory();
	}

	@Override
	public final READER getReader() {
		return reader;
	}

	@Override
	public final SENDER getSender() {
		return sender;
	}

}
