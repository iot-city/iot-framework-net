package org.iotcity.iot.framework.net.serialization.bytes.impls;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.iotcity.iot.framework.core.util.pool.ObjectPool;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.config.NetConfigSerialization;
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
	 * The configuration creation method.
	 */
	private static Method creationMethod;
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
				creationMethod = FST_CLASS.getMethod("createDefaultConfiguration");
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

	// ------------------------------------- Private fields -------------------------------------

	/**
	 * The register classes for each instance.
	 */
	private Class<?>[] classes = null;
	/**
	 * The maximum capacity of free objects to store the serialization instance in object pool.
	 */
	private int capacity = 8;
	/**
	 * The object pool for multi-threading objects.
	 */
	private ObjectPool<FSTObject> pool;
	/**
	 * The default FST instance.
	 */
	private FSTObject instance;

	// ------------------------------------- Constructor -------------------------------------

	/**
	 * Constructor for FST BYTES converter.
	 */
	public FSTBytes() {
		// Create the pool.
		pool = createPool(capacity);
		// Create the default instance.
		instance = createFSTObject();
	}

	// ------------------------------------- Private methods -------------------------------------

	/**
	 * Create the object pool (returns null if no capacity).
	 * @param capacity The maximum capacity of free objects to store the serialization instance in object pool.
	 * @return The object pool instance.
	 */
	private ObjectPool<FSTObject> createPool(final int capacity) {
		// Create a new thread safe object pool.
		return new ObjectPool<FSTObject>(true, false, capacity) {

			private AtomicInteger creation = new AtomicInteger();

			@Override
			protected FSTObject create() {
				if (creation.get() >= capacity || creation.incrementAndGet() > capacity) {
					return instance;
				} else {
					return createFSTObject();
				}
			}

			@Override
			protected void reset(FSTObject object) {
			}

		};
	}

	/**
	 * Create a FST object.
	 */
	private FSTObject createFSTObject() {
		Object config = null;
		try {
			// Create the instance.
			config = creationMethod.invoke(null);
		} catch (Exception e) {
			FrameworkNet.getLogger().error(e);
		}
		return new FSTObject(config, classes);
	}

	// ------------------------------------- Override methods -------------------------------------

	@Override
	public boolean config(NetConfigSerialization data, boolean reset) {
		if (data == null || data.capacity == capacity || data.capacity <= 0) return true;
		capacity = data.capacity;
		ObjectPool<FSTObject> prev = pool;
		pool = createPool(capacity);
		prev.destroy();
		return true;
	}

	@Override
	public void register(Class<?>... classes) {
		if (classes == null || this.classes == classes) return;
		this.classes = classes;
	}

	@Override
	public byte[] serialize(Serializable obj) throws Exception {
		if (obj == null) return null;
		FSTObject fst = pool.obtain();
		fst.registerClasses(classes);
		byte[] bs = (byte[]) serializeMethod.invoke(fst.config, obj);
		if (fst != instance) pool.free(fst);
		return bs;
	}

	@Override
	public <T extends Serializable> T deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) return null;
		FSTObject fst = pool.obtain();
		fst.registerClasses(classes);
		@SuppressWarnings("unchecked")
		T obj = (T) deserializeMethod.invoke(fst.config, bytes);
		if (fst != instance) pool.free(fst);
		return obj;
	}

	/**
	 * The FST object data.
	 * @author ardon
	 * @date 2021-09-15
	 */
	final static class FSTObject {

		/**
		 * Registered class.
		 */
		private Class<?>[] classes;
		/**
		 * The locker for class register.
		 */
		private final Object lock = new Object();
		/**
		 * The FST configuration object.
		 */
		private Object config;

		/**
		 * Constructor for FST object data.
		 * @param config The FST configuration object.
		 * @param classes The classes to be registered.
		 */
		FSTObject(Object config, Class<?>[] classes) {
			this.config = config;
			registerClasses(classes);
		}

		/**
		 * Register classes for current FST object.
		 * @param classes The classes to be registered.
		 */
		void registerClasses(Class<?>[] classes) {
			if (classes == null || this.classes == classes) return;
			synchronized (lock) {
				if (this.classes == classes) return;
				this.classes = classes;
				try {
					registerMethod.invoke(config, (Object) classes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
