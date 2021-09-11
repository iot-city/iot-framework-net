package org.iotcity.iot.framework.net.serialization.json.impls;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.serialization.json.JSON;
import org.iotcity.iot.framework.net.serialization.json.JSONFactory;

/**
 * The JSON converter for GsonJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class GsonJSON implements JSON {

	/**
	 * The GSON object class.
	 */
	private static Class<?> gsonClass;
	/**
	 * To JSON string method.
	 */
	private static Method toJSONMethod;
	/**
	 * To java object method.
	 */
	private static Method toObjectMethod;
	/**
	 * To java object by type method.
	 */
	private static Method toObjectMethodByType;
	/**
	 * Parse JSON array method.
	 */
	private static Method parseArrayMethod;
	/**
	 * To JSON array method.
	 */
	private static Method toArrayMethod;
	/**
	 * Get array object method.
	 */
	private static Method getArrayObjectMethod;
	/**
	 * Get array object by type method.
	 */
	private static Method getArrayObjectMethodByType;

	static {
		// Check for valid class.
		if (JSONFactory.getJSONClass().equals(GsonJSON.class)) {
			try {
				// Gets JSON methods.
				gsonClass = Class.forName("com.google.gson.Gson");
				toJSONMethod = gsonClass.getMethod("toJson", new Class[] {
					Object.class
				});
				toObjectMethod = gsonClass.getMethod("fromJson", new Class[] {
					String.class,
					Class.class
				});
				toObjectMethodByType = gsonClass.getMethod("fromJson", new Class[] {
					String.class,
					Type.class
				});
				Class<?> clazz = Class.forName("com.google.gson.JsonParser");
				parseArrayMethod = clazz.getMethod("parseString", new Class[] {
					String.class
				});
				clazz = Class.forName("com.google.gson.JsonElement");
				toArrayMethod = clazz.getMethod("getAsJsonArray");
				getArrayObjectMethod = gsonClass.getMethod("fromJson", new Class[] {
					clazz,
					Class.class
				});
				getArrayObjectMethodByType = gsonClass.getMethod("fromJson", new Class[] {
					clazz,
					Type.class
				});
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * The framework logger.
	 */
	private final Logger logger = FrameworkNet.getLogger();
	/**
	 * The GSON object.
	 */
	private final Object gson;

	/**
	 * Constructor for GsonJSON converter.
	 */
	public GsonJSON() {
		Object obj = null;
		try {
			obj = gsonClass.getConstructor().newInstance();
		} catch (Exception e) {
			logger.error(e);
		}
		this.gson = obj;
	}

	@Override
	public void config(PropertiesMap<Object> data) {
	}

	@Override
	public String toJSONString(Object obj) {
		if (obj == null) return null;
		try {
			return (String) toJSONMethod.invoke(gson, new Object[] {
				obj
			});
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public <T> T toJavaObject(Class<T> clazz, String str) {
		if (str == null) return null;
		try {
			@SuppressWarnings("unchecked")
			T obj = (T) toObjectMethod.invoke(gson, new Object[] {
				str,
				clazz
			});
			return obj;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public <T> T toJavaObject(Type type, String str) {
		if (str == null) return null;
		try {
			@SuppressWarnings("unchecked")
			T obj = (T) toObjectMethodByType.invoke(gson, new Object[] {
				str,
				type
			});
			return obj;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public <T> T[] toJavaArray(Class<T> arrayClass, Class<?>[] classes, String str) {
		if (classes == null || classes.length == 0 || StringHelper.isEmpty(str)) return null;
		int len = classes.length;
		try {
			Object ele = parseArrayMethod.invoke(null, str);
			Iterable<?> jsonArray = (Iterable<?>) toArrayMethod.invoke(ele);
			@SuppressWarnings("unchecked")
			T[] rets = (T[]) Array.newInstance(arrayClass, len);
			int i = 0;
			for (Object e : jsonArray) {
				if (i >= len) break;
				@SuppressWarnings("unchecked")
				T obj = (T) getArrayObjectMethod.invoke(gson, e, classes[i]);
				rets[i] = obj;
				i++;
			}
			return rets;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public <T> T[] toJavaArray(Class<T> arrayClass, Type[] types, String str) {
		if (types == null || types.length == 0 || StringHelper.isEmpty(str)) return null;
		int len = types.length;
		try {
			Object ele = parseArrayMethod.invoke(null, str);
			Iterable<?> jsonArray = (Iterable<?>) toArrayMethod.invoke(ele);
			@SuppressWarnings("unchecked")
			T[] rets = (T[]) Array.newInstance(arrayClass, len);
			int i = 0;
			for (Object e : jsonArray) {
				if (i >= len) break;
				@SuppressWarnings("unchecked")
				T obj = (T) getArrayObjectMethodByType.invoke(gson, e, types[i]);
				rets[i] = obj;
				i++;
			}
			return rets;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

}
