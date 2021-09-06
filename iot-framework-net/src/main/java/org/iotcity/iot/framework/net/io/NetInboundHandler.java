package org.iotcity.iot.framework.net.io;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Network inbound handler for data processing.
 * @param <IO> The network I/O class type.
 * @param <DATA> The network inbound data type.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetInboundHandler<IO extends NetIO<?, ?>, DATA extends NetData> implements NetInbound<IO, DATA> {

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
	 * Constructor for network inbound handler.
	 */
	public NetInboundHandler() {
		Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
		this.ioClass = (Class<?>) types[0];
		this.dataClass = (Class<?>) types[1];
	}

	/**
	 * Constructor for network inbound handler.
	 * @param ioClass The network I/O object class.
	 * @param dataClass The network data object class.
	 */
	public NetInboundHandler(Class<IO> ioClass, Class<DATA> dataClass) {
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
	public final boolean filterIO(NetIO<?, ?> io) {
		return filter((IO) io);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final NetData readIO(NetIO<?, ?> io) throws Exception {
		return read((IO) io);
	}

	// --------------------------- Abstract methods ----------------------------

	/**
	 * Filter the I/O object for this inbound message (returns true if the data can be read, otherwise, return false).
	 * @param io The network I/O object (not null).
	 * @return Returns true if the data can be read, otherwise, return false.
	 */
	public abstract boolean filter(IO io);

	/**
	 * Read data from network I/O object (returns null when no data is read).
	 * @param io The network I/O object (not null).
	 * @return The network data that has been read.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public abstract DATA read(IO io) throws Exception;

}
