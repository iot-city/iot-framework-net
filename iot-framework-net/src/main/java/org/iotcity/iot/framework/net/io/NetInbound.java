package org.iotcity.iot.framework.net.io;

/**
 * Network inbound data processing object.
 * @param <IO> The network I/O class type.
 * @param <DATA> The network inbound data type.
 * @author ardon
 * @date 2021-06-16
 */
public interface NetInbound<IO extends NetIO<?, ?>, DATA extends NetData> {

	/**
	 * Gets the network I/O object class.
	 */
	Class<?> getIOClass();

	/**
	 * Gets the network data object class.
	 */
	Class<?> getDataClass();

	/**
	 * Filter the I/O object for this inbound message (returns true if the data can be read, otherwise, return false).
	 * @param io The network I/O object.
	 * @return Returns true if the data can be read, otherwise, return false.
	 */
	boolean filterIO(NetIO<?, ?> io);

	/**
	 * Read data from network I/O object (returns null when no data is read).
	 * @param io The network I/O object.
	 * @return The network data that has been read.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	NetData readIO(NetIO<?, ?> io) throws Exception;

}
