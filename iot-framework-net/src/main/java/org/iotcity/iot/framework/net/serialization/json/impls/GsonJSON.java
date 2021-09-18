package org.iotcity.iot.framework.net.serialization.json.impls;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;

import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.config.NetConfigSerialization;
import org.iotcity.iot.framework.net.serialization.json.JSON;

/**
 * The JSON converter for GsonJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class GsonJSON implements JSON {

	// ------------------------------------- Static fields -------------------------------------

	/**
	 * The GSON class.
	 */
	public static final Class<?> JSON_CLASS = getJSONClass();

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
		if (JSON_CLASS != null) {
			try {
				// Gets JSON methods.
				toJSONMethod = JSON_CLASS.getMethod("toJson", new Class[] {
					Object.class
				});
				toObjectMethod = JSON_CLASS.getMethod("fromJson", new Class[] {
					String.class,
					Class.class
				});
				toObjectMethodByType = JSON_CLASS.getMethod("fromJson", new Class[] {
					String.class,
					Type.class
				});
				Class<?> clazz = Class.forName("com.google.gson.JsonParser");
				parseArrayMethod = clazz.getMethod("parseString", new Class[] {
					String.class
				});
				clazz = Class.forName("com.google.gson.JsonElement");
				toArrayMethod = clazz.getMethod("getAsJsonArray");
				getArrayObjectMethod = JSON_CLASS.getMethod("fromJson", new Class[] {
					clazz,
					Class.class
				});
				getArrayObjectMethodByType = JSON_CLASS.getMethod("fromJson", new Class[] {
					clazz,
					Type.class
				});
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}
		}
	}

	/**
	 * Gets current JSON class.
	 */
	private final static Class<?> getJSONClass() {
		try {
			return Class.forName("com.google.gson.Gson");
		} catch (Exception e) {
			return null;
		}
	}

	// ------------------------------------- Private fields -------------------------------------

	/**
	 * The GSON object.
	 */
	private Object gson;

	// ------------------------------------- Constructor -------------------------------------

	/**
	 * Constructor for GsonJSON converter.
	 */
	public GsonJSON() {
		try {
			gson = JSON_CLASS.getConstructor().newInstance();
		} catch (Exception e) {
			FrameworkNet.getLogger().error(e);
		}
	}

	// ------------------------------------- Override methods -------------------------------------

	@Override
	public boolean config(NetConfigSerialization data, boolean reset) {
		if (data == null) return true;
		String dateFormat = StringHelper.isEmptyWithTrim(data.dateFormat) ? null : data.dateFormat;
		try {
			Class<?> bclass = Class.forName("com.google.gson.GsonBuilder");
			Method method = JSON_CLASS.getMethod("newBuilder");
			Object builder = method.invoke(this.gson);
			if (dateFormat == null) {
				method = bclass.getMethod("setDateFormat", int.class, int.class);
				method.invoke(builder, DateFormat.DEFAULT, DateFormat.DEFAULT);
			} else {
				method = bclass.getMethod("setDateFormat", String.class);
				method.invoke(builder, dateFormat);
			}
			method = bclass.getMethod("create");
			this.gson = method.invoke(builder);
			return true;
		} catch (Exception e) {
			FrameworkNet.getLogger().error(e);
			return false;
		}
	}

	@Override
	public String toJSONString(Object obj) throws Exception {
		if (obj == null) return null;
		return (String) toJSONMethod.invoke(gson, obj);
	}

	@Override
	public <T> T toObject(Class<T> clazz, String str) throws Exception {
		if (str == null) return null;
		@SuppressWarnings("unchecked")
		T obj = (T) toObjectMethod.invoke(gson, str, clazz);
		return obj;
	}

	@Override
	public <T> T toObject(Type type, String str) throws Exception {
		if (str == null) return null;
		@SuppressWarnings("unchecked")
		T obj = (T) toObjectMethodByType.invoke(gson, str, type);
		return obj;
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Class<?>[] classes, String str) throws Exception {
		if (classes == null || classes.length == 0 || StringHelper.isEmpty(str)) return null;
		int len = classes.length;
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
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Type[] types, String str) throws Exception {
		if (types == null || types.length == 0 || StringHelper.isEmpty(str)) return null;
		int len = types.length;
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
	}

}
