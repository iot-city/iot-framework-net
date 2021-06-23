package org.iotcity.iot.framework.net.io;

/**
 * Network message processing object used to process inbound data.
 * @author ardon
 * @date 2021-06-21
 */
public class NetMessager {

	/**
	 * Use the network I/O object to read and process inbound data.
	 * @param io The network input and output object.
	 * @param asyncMode Indicates whether the asynchronous processing mode is used. If it is set to true, the asynchronous processing mode is used. otherwise, the synchronous processing mode is used.
	 * @return The message process status.
	 */
	public NetMessageStatus onMessage(NetIO<?, ?, ?> io, boolean asyncMode) {
		return null;
	}

}
