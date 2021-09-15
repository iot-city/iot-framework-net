package org.iotcity.iot.framework.net.serialization.bytes;

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
	 * The locker for BYTES class.
	 */
	private static final Object classLock = new Object();
	/**
	 * BYTES converter class.
	 */
	private static Class<?> bytesClass;
	/**
	 * The locker for instance.
	 */
	private static final Object instanceLock = new Object();
	/**
	 * BYTES default converter object.
	 */
	private static BYTES instance;

	/**
	 * Set the default BYTES converter class (the default BYTES converter class will be replaced by this new one).
	 * @param clazz The BYTES converter class.
	 */
	public static final void setDefaultBytesClass(Class<BYTES> clazz) {
		if (clazz == null) return;
		synchronized (classLock) {
			bytesClass = clazz;
		}
	}

	/**
	 * Gets the default supported BYTES class (never null).
	 */
	public static final Class<?> getDefaultBytesClass() {
		if (bytesClass != null) return bytesClass;
		synchronized (classLock) {
			if (bytesClass != null) return bytesClass;

			if (KryoBytes.KRYO_CLASS != null) {
				bytesClass = KryoBytes.class;
			} else if (FSTBytes.FST_CLASS != null) {
				bytesClass = FSTBytes.class;
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
		synchronized (instanceLock) {
			instance = bytes;
		}
	}

	/**
	 * Gets the default BYTES converter object (returns not null).
	 */
	public static final BYTES getDefaultBytes() {
		if (instance != null) return instance;
		synchronized (instanceLock) {
			if (instance != null) return instance;
			instance = newBytes();
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
