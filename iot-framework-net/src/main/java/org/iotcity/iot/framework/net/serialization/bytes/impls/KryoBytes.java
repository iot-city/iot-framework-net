package org.iotcity.iot.framework.net.serialization.bytes.impls;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.serialization.bytes.BYTES;

/**
 * The BYTES converter for KRYO.
 * @author ardon
 * @date 2021-09-15
 */
public class KryoBytes implements BYTES {

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
	 * Input close method.
	 */
	private static Method inputClloseMethod;
	/**
	 * Output class.
	 */
	private static Class<?> outputClass;
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
				inputClloseMethod = inputClass.getMethod("close");
				outputClass = Class.forName("com.esotericsoftware.kryo.io.Output");
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
	 * KRYO is not thread safe. Each thread should have its own instance.
	 */
	private static final ThreadLocal<Object> kryos = new ThreadLocal<Object>() {

		protected Object initialValue() {
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
			return obj;
		};

	};

	// ------------------------------------- Constructor -------------------------------------

	@Override
	public void register(Class<?>... classes) {
		if (classes == null || classes.length == 0) return;
		Object kryo = kryos.get();
		try {
			for (Class<?> clazz : classes) {
				registerMethod.invoke(kryo, (Object) clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] serialize(Serializable obj) throws Exception {
		if (obj == null) return null;
		Object output = outputClass.getConstructor(OutputStream.class).newInstance(new ByteArrayOutputStream());
		writeObjectMethod.invoke(kryos.get(), output, obj);
		byte[] bs = (byte[]) outputToArrayMethod.invoke(output);
		outputCloseMethod.invoke(output);
		return bs;
	}

	@Override
	public <T extends Serializable> T deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) return null;
		Object input = inputClass.getConstructor(byte[].class).newInstance(bytes);
		@SuppressWarnings("unchecked")
		T obj = (T) readObjectMethod.invoke(kryos.get(), input);
		inputClloseMethod.invoke(input);
		return obj;
	}

}
