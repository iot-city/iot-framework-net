package org.iotcity.iot.framework.net.serialization.bytes;

import org.iotcity.iot.framework.net.config.NetConfigSerialization;
import org.iotcity.iot.framework.net.serialization.bytes.impls.FSTBytes;
import org.iotcity.iot.framework.net.serialization.bytes.impls.JavaBytes;
import org.iotcity.iot.framework.net.serialization.bytes.impls.KryoBytes;

/**
 * The BYTES converter object factory.
 * @author ardon
 * @date 2021-09-15
 */
public final class BYTESFactory {

	/**
	 * The global configuration data for converters.
	 */
	private static final NetConfigSerialization config = new NetConfigSerialization();
	/**
	 * The locker for BYTES.
	 */
	private static final Object lcok = new Object();
	/**
	 * BYTES converter class.
	 */
	private static Class<?> bytesClass;
	/**
	 * BYTES default converter object.
	 */
	private static BYTES instance;

	/**
	 * Set the default configuration data for default BYTES converter.
	 * @param data The configuration data.
	 */
	public static final void setDefaultConfiguration(NetConfigSerialization data) {
		if (data == null) return;
		config.capacity = data.capacity;
		config.dateFormat = data.dateFormat;
		BYTES obj = instance;
		if (obj != null) obj.config(config, false);
	}

	/**
	 * Set the default BYTES converter class (the default BYTES converter class will be replaced by this new one).
	 * @param clazz The BYTES converter class.
	 */
	public static final void setDefaultBytesClass(Class<BYTES> clazz) {
		if (clazz == null) return;
		synchronized (lcok) {
			bytesClass = clazz;
			instance = null;
		}
	}

	/**
	 * Gets the default supported BYTES class (never null).
	 */
	public static final Class<?> getDefaultBytesClass() {
		if (bytesClass != null) return bytesClass;
		synchronized (lcok) {
			if (bytesClass != null) return bytesClass;

			if (FSTBytes.FST_CLASS != null) {
				bytesClass = FSTBytes.class;
			} else if (KryoBytes.KRYO_CLASS != null) {
				bytesClass = KryoBytes.class;
			} else {
				bytesClass = JavaBytes.class;
			}

		}
		return bytesClass;
	}

	/**
	 * Set the default BYTES converter instance (the default BYTES converter instance will be replaced by this new one).
	 * @param bytes BYTES converter instance (required, not null).
	 */
	public static final void setDefaultBytes(BYTES bytes) {
		if (bytes == null) return;
		synchronized (lcok) {
			instance = bytes;
		}
	}

	/**
	 * Gets the default BYTES converter object (returns not null).
	 */
	public static final BYTES getDefaultBytes() {
		if (instance != null) return instance;
		synchronized (lcok) {
			if (instance != null) return instance;
			instance = newBytes();
			instance.config(config, false);
		}
		return instance;
	}

	/**
	 * New a BYTES converter instance by default BYTES class (returns not null).
	 * @return BYTES converter object.
	 */
	public static final BYTES newBytes() {
		try {
			return (BYTES) getDefaultBytesClass().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return new JavaBytes();
		}
	}

}
