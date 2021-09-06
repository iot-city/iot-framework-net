package org.iotcity.iot.framework.net.serialization.json.impls;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.serialization.json.JSON;

/**
 * The JSON converter for GsonJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class GsonJSON implements JSON {

	/**
	 * The framework logger.
	 */
	private final Logger logger = FrameworkNet.getLogger();

	/**
	 * The GSON object.
	 */
	private Object gson;
	/**
	 * To JSON string method.
	 */
	private Method toJSONMethod;
	/**
	 * To java object method.
	 */
	private Method toObjectMethod;
	/**
	 * Parse JSON array method.
	 */
	private Method parseArrayMethod;
	/**
	 * To JSON array method.
	 */
	private Method toArrayMethod;
	/**
	 * Get array object method.
	 */
	private Method getArrayObjectMethod;

	/**
	 * Constructor for GsonJSON converter.
	 */
	public GsonJSON() {
		try {
			Class<?> gsonClass = Class.forName("com.google.gson.Gson");
			gson = gsonClass.getConstructor().newInstance();
			toJSONMethod = gsonClass.getMethod("toJson", new Class[] {
				Object.class
			});
			toObjectMethod = gsonClass.getMethod("fromJson", new Class[] {
				String.class,
				Class.class
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
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void config(PropertiesMap<Object> data) {
	}

	@Override
	public String toJSONString(Object obj) {
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
	public <T> T[] toJavaArray(Class<T> arrayClass, Class<?>[] classes, String str) {
		if (classes == null || classes.length == 0 || StringHelper.isEmpty(str)) return null;
		int len = classes.length;

		// The code equals:
		// JsonElement ele = JsonParser.parseString(str);
		// JsonArray jsonArray = ele.getAsJsonArray();
		// for (Object e : jsonArray) {
		// ...
		// T obj = (T) gson.fromJson(e, classes[i]);
		// ...
		// }

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

}
