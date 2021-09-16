package org.iotcity.iot.framework.net.serialization.bytes.impls;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.serialization.bytes.BYTES;

/**
 * The BYTES converter for FST.
 * @author ardon
 * @date 2021-09-15
 */
public final class FSTBytes implements BYTES {

	// ------------------------------------- Static fields -------------------------------------

	/**
	 * The FST class.
	 */
	public final static Class<?> FST_CLASS = getFSTClass();

	/**
	 * The FST configuration object.
	 */
	private static Object config;
	/**
	 * Register the classes method.
	 */
	private static Method registerMethod;
	/**
	 * To byte array method.
	 */
	private static Method serializeMethod;
	/**
	 * To java object method.
	 */
	private static Method deserializeMethod;

	static {
		// Check for valid class.
		if (FST_CLASS != null) {
			try {
				// Gets creation method.
				Method creationMethod = FST_CLASS.getMethod("createDefaultConfiguration");
				// Create the instance.
				config = creationMethod.invoke(null);
				// Gets BYTES methods.
				registerMethod = FST_CLASS.getMethod("registerClass", new Class[] {
					Class[].class
				});
				serializeMethod = FST_CLASS.getMethod("asByteArray", new Class[] {
					Object.class
				});
				deserializeMethod = FST_CLASS.getMethod("asObject", new Class[] {
					byte[].class
				});
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * Gets current FST class.
	 */
	private final static Class<?> getFSTClass() {
		try {
			return Class.forName("org.nustaq.serialization.FSTConfiguration");
		} catch (Exception e) {
			return null;
		}
	}

	// ------------------------------------- Override methods -------------------------------------

	@Override
	public void register(Class<?>... classes) {
		if (classes == null || classes.length == 0) return;
		try {
			registerMethod.invoke(config, (Object) classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] serialize(Serializable obj) throws Exception {
		if (obj == null) return null;
		return (byte[]) serializeMethod.invoke(config, obj);
	}

	@Override
	public <T extends Serializable> T deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) return null;
		@SuppressWarnings("unchecked")
		T obj = (T) deserializeMethod.invoke(config, bytes);
		return obj;
	}

}
