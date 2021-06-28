package org.iotcity.iot.framework.net.io;

/**
 * Network outbound data processing object.
 * @param <IO> The network I/O class type.
 * @param <DATA> The network outbound data type.
 * @author ardon
 * @date 2021-06-16
 */
public interface NetOutbound<IO extends NetIO<?, ?>, DATA extends NetData> {

	/**
	 * Gets the network I/O object class.
	 */
	Class<?> getIOClass();

	/**
	 * Gets the network data object class.
	 */
	Class<?> getDataClass();

	/**
	 * Filter the I/O object for this outbound message (returns true if the data can be sent to remote end, otherwise, return false).
	 * @param io The network I/O object.
	 * @param data Data that needs to be sent to the remote end.
	 * @return Returns true if the data can be sent to remote end, otherwise, return false.
	 */
	boolean filterIO(NetIO<?, ?> io, NetData data);

	/**
	 * Use the I/O object to send a message to the remote end.
	 * @param io The network I/O object.
	 * @param data Data that needs to be sent to the remote end.
	 * @return The message process status.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	NetMessageStatus sendIO(NetIO<?, ?> io, NetData data) throws Exception;

}
