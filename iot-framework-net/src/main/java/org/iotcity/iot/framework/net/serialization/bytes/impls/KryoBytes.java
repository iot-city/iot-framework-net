package org.iotcity.iot.framework.net.serialization.bytes.impls;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.iotcity.iot.framework.core.util.pool.ObjectPool;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.config.NetConfigSerialization;
import org.iotcity.iot.framework.net.serialization.bytes.BYTES;

/**
 * The BYTES converter for KRYO.
 * @author ardon
 * @date 2021-09-15
 */
public final class KryoBytes implements BYTES {

	// ------------------------------------- Static fields -------------------------------------

	/**
	 * The KRYO class.
	 */
	public static final Class<?> KRYO_CLASS = getKryoClass();

	/**
	 * Input class.
	 */
	private static Class<?> inputClass;
	/**
	 * The constructor of input class.
	 */
	private static Constructor<?> inputConstructor;
	/**
	 * Input close method.
	 */
	private static Method inputClloseMethod;
	/**
	 * Output class.
	 */
	private static Class<?> outputClass;
	/**
	 * The constructor of output class.
	 */
	private static Constructor<?> outputConstructor;
	/**
	 * Output to array method.
	 */
	private static Method outputToArrayMethod;
	/**
	 * Output close method.
	 */
	private static Method outputCloseMethod;
	/**
	 * KRYO register method.
	 */
	private static Method registerMethod;
	/**
	 * KRYO write object method.
	 */
	private static Method writeObjectMethod;
	/**
	 * KRYO read object method.
	 */
	private static Method readObjectMethod;

	static {
		// Check for valid class.
		if (KRYO_CLASS != null) {
			try {
				// Input and output methods.
				inputClass = Class.forName("com.esotericsoftware.kryo.io.Input");
				inputConstructor = inputClass.getConstructor(byte[].class);
				inputClloseMethod = inputClass.getMethod("close");
				outputClass = Class.forName("com.esotericsoftware.kryo.io.Output");
				outputConstructor = outputClass.getConstructor(OutputStream.class);
				outputToArrayMethod = outputClass.getMethod("toBytes");
				outputCloseMethod = outputClass.getMethod("close");
				// Gets BYTES methods.
				registerMethod = KRYO_CLASS.getMethod("register", new Class[] {
					Class.class
				});
				writeObjectMethod = KRYO_CLASS.getMethod("writeClassAndObject", new Class[] {
					outputClass,
					Object.class
				});
				readObjectMethod = KRYO_CLASS.getMethod("readClassAndObject", new Class[] {
					inputClass
				});
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * Gets KRYO class.
	 */
	private final static Class<?> getKryoClass() {
		try {
			return Class.forName("com.esotericsoftware.kryo.Kryo");
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
	private ObjectPool<KryoObject> pool;
	/**
	 * The default KRYO instance.
	 */
	private KryoObject instance;

	// ------------------------------------- Constructor -------------------------------------

	/**
	 * Constructor for KRYO BYTES converter.
	 */
	public KryoBytes() {
		// Create the pool.
		pool = createPool(capacity);
		// Create the default instance.
		instance = createKRYOObject();
	}

	// ------------------------------------- Private methods -------------------------------------

	/**
	 * Create the object pool (returns null if no capacity).
	 * @param capacity The maximum capacity of free objects to store the serialization instance in object pool.
	 * @return The object pool instance.
	 */
	private ObjectPool<KryoObject> createPool(final int capacity) {
		// Create a new thread safe object pool.
		return new ObjectPool<KryoObject>(true, false, capacity) {

			private AtomicInteger creation = new AtomicInteger();

			@Override
			protected KryoObject create() {
				if (creation.get() >= capacity || creation.incrementAndGet() > capacity) {
					return instance;
				} else {
					return createKRYOObject();
				}
			}

			@Override
			protected void reset(KryoObject object) {
			}

		};
	}

	/**
	 * Create a KRYO object.
	 */
	private KryoObject createKRYOObject() {
		Object obj = null;
		try {
			obj = KRYO_CLASS.getConstructor().newInstance();
			Method regs = KRYO_CLASS.getMethod("setRegistrationRequired", new Class[] {
				boolean.class
			});
			regs.invoke(obj, false);
		} catch (Exception e) {
			FrameworkNet.getLogger().error(e);
		}
		return new KryoObject(obj, classes);
	}

	// ------------------------------------- Override methods -------------------------------------

	@Override
	public boolean config(NetConfigSerialization data, boolean reset) {
		if (data == null || data.capacity == capacity || data.capacity <= 0) return true;
		capacity = data.capacity;
		ObjectPool<KryoObject> prev = pool;
		pool = createPool(capacity);
		prev.destroy();
		return true;
	}

	@Override
	public void register(Class<?>... classes) {
		if (classes == null || classes.length == 0) return;
		this.classes = classes;
	}

	@Override
	public byte[] serialize(Serializable obj) throws Exception {
		if (obj == null) return null;
		KryoObject kryo = pool.obtain();
		kryo.registerClasses(classes);
		Object output = outputConstructor.newInstance(new ByteArrayOutputStream());
		if (kryo == instance) {
			synchronized (kryo) {
				writeObjectMethod.invoke(kryo.kryo, output, obj);
			}
		} else {
			writeObjectMethod.invoke(kryo.kryo, output, obj);
			pool.free(kryo);
		}
		byte[] bs = (byte[]) outputToArrayMethod.invoke(output);
		outputCloseMethod.invoke(output);
		return bs;
	}

	@Override
	public <T extends Serializable> T deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) return null;
		KryoObject kryo = pool.obtain();
		kryo.registerClasses(classes);
		Object input = inputConstructor.newInstance(bytes);
		Object obj;
		if (kryo == instance) {
			synchronized (kryo) {
				obj = readObjectMethod.invoke(kryo.kryo, input);
			}
		} else {
			obj = readObjectMethod.invoke(kryo.kryo, input);
			pool.free(kryo);
		}
		inputClloseMethod.invoke(input);
		@SuppressWarnings("unchecked")
		T ret = (T) obj;
		return ret;
	}

	/**
	 * The KRYO object data.
	 * @author ardon
	 * @date 2021-09-15
	 */
	final static class KryoObject {

		/**
		 * Registered class.
		 */
		private Class<?>[] classes;
		/**
		 * The locker for class register.
		 */
		private final Object lock = new Object();
		/**
		 * The KRYO object.
		 */
		private final Object kryo;

		/**
		 * Constructor for KRYO object data.
		 * @param kryo The KRYO object.
		 * @param classes The classes to be registered.
		 */
		KryoObject(Object kryo, Class<?>[] classes) {
			this.kryo = kryo;
			registerClasses(classes);
		}

		/**
		 * Register classes for current KRYO object.
		 * @param classes The classes to be registered.
		 */
		void registerClasses(Class<?>[] classes) {
			if (classes == null || this.classes == classes) return;
			synchronized (lock) {
				if (this.classes == classes) return;
				this.classes = classes;
				try {
					for (Class<?> clazz : classes) {
						registerMethod.invoke(kryo, (Object) clazz);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
