package org.iotcity.iot.framework.net.io;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Network outbound handler for message sending.
 * @param <IO> The network I/O class type.
 * @param <DATA> The network outbound data type.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetOutboundHandler<IO extends NetIO<?, ?>, DATA extends NetData> implements NetOutbound<IO, DATA> {

	// --------------------------- Private fields ----------------------------

	/**
	 * The network I/O object class.
	 */
	private final Class<?> ioClass;
	/**
	 * The network data object class.
	 */
	private final Class<?> dataClass;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for network outbound handler.
	 */
	public NetOutboundHandler() {
		Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
		this.ioClass = (Class<?>) types[0];
		this.dataClass = (Class<?>) types[1];
	}

	/**
	 * Constructor for network outbound handler.
	 * @param ioClass The network I/O object class.
	 * @param dataClass The network data object class.
	 */
	public NetOutboundHandler(Class<IO> ioClass, Class<DATA> dataClass) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final Class<?> getIOClass() {
		return ioClass;
	}

	@Override
	public final Class<?> getDataClass() {
		return dataClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final boolean filterIO(NetIO<?, ?> io, NetData data) {
		return filter((IO) io, (DATA) data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final NetMessageStatus sendIO(NetIO<?, ?> io, NetData data, long timeout) throws Exception {
		return send((IO) io, (DATA) data, timeout);
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Filter the I/O object for this outbound message (returns true if the data can be sent to remote end, otherwise, return false).
	 * @param io The network I/O object (not null).
	 * @param data Data that needs to be sent to the remote end (not null).
	 * @return Returns true if the data can be sent to remote end, otherwise, return false.
	 */
	public abstract boolean filter(IO io, DATA data);

	/**
	 * Use the I/O object to send a message to the remote end.
	 * @param io The network I/O object (not null).
	 * @param data Data that needs to be sent to the remote end (not null).
	 * @param timeout The timeout value in milliseconds. <br/>
	 *            1. If it is sending a request data to the remote end, this value is the timeout value that waiting for a response data from remote end. <br/>
	 *            2. If it is sending a response data to the remote end, this value will be 0.
	 * @return The message process status.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public abstract NetMessageStatus send(IO io, DATA data, long timeout) throws Exception;

}
