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
public abstract class NetOutboundHandler<IO extends NetIO<?, ?, ?>, DATA extends NetData> implements NetOutbound<IO, DATA> {

	/**
	 * The network I/O object class.
	 */
	private final Class<?> ioClass;
	/**
	 * The network data object class.
	 */
	private final Class<?> dataClass;

	/**
	 * Constructor for network outbound handler.
	 */
	public NetOutboundHandler() {
		Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
		this.ioClass = (Class<?>) types[0];
		this.dataClass = (Class<?>) types[1];
	}

	@Override
	public Class<?> getIOClass() {
		return ioClass;
	}

	@Override
	public Class<?> getDataClass() {
		return dataClass;
	}

}
