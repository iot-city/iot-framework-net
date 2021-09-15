package org.iotcity.iot.framework.net.serialization.bytes;

import java.io.Serializable;

/**
 * The serializable converter for serialization.
 * @author ardon
 * @date 2021-09-15
 */
public interface BYTES {

	/**
	 * Register the data types that need to be serialized.
	 * @param classes The data types array.
	 */
	void register(Class<?>... classes);

	/**
	 * Serialize an object to bytes.
	 * @param obj The serializable object.
	 * @return The object bytes data.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	byte[] serialize(Serializable obj) throws Exception;

	/**
	 * Deserialize byte array to an object.
	 * @param <T> The serializable object type.
	 * @param bytes The object bytes data.
	 * @return The serializable object.
	 * @throws Exception An error will be thrown when an exception is encountered during execution.
	 */
	<T extends Serializable> T deserialize(byte[] bytes) throws Exception;

}
