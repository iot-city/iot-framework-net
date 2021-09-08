package org.iotcity.iot.framework.net.serialization.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The serializable helper for serialization.
 * @author ardon
 * @date 2021-08-29
 */
public final class SerializableHelper {

	/**
	 * Serialize the serializable object to bytes.
	 * @param obj The serializable object.
	 * @return The object bytes data.
	 * @throws IOException Any exception thrown by the underlying OutputStream.
	 */
	public static final byte[] serialize(Serializable obj) throws IOException {
		if (obj == null) return null;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = null;
		try {
			o = new ObjectOutputStream(b);
			o.writeObject(obj);
			return b.toByteArray();
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (o != null) {
				try {
					o.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Deserialize byte array to serializable object.
	 * @param <T> The serializable object type.
	 * @param bytes The object bytes data.
	 * @return The serializable object.
	 * @throws IOException Any exception thrown by the underlying InputStream.
	 * @throws ClassNotFoundException Class of a serialized object cannot be found.
	 */
	public static final <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = null;
		try {
			o = new ObjectInputStream(b);
			@SuppressWarnings("unchecked")
			T ret = (T) o.readObject();
			return ret;
		} finally {
			if (b != null) {
				try {
					b.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (o != null) {
				try {
					o.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
