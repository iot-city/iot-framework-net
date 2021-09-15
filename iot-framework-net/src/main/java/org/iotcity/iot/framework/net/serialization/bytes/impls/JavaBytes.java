package org.iotcity.iot.framework.net.serialization.bytes.impls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.iotcity.iot.framework.net.serialization.bytes.BYTES;

/**
 * The BYTES converter for Java serializable object.
 * @author ardon
 * @date 2021-09-15
 */
public final class JavaBytes implements BYTES {

	@Override
	public void register(Class<?>... classes) {
	}

	@Override
	public byte[] serialize(Serializable obj) throws Exception {
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

	@Override
	public <T extends Serializable> T deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) return null;
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
