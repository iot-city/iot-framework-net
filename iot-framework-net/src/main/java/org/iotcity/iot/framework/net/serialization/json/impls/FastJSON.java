package org.iotcity.iot.framework.net.serialization.json.impls;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.serialization.json.JSON;

/**
 * The JSON converter for FastJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class FastJSON implements JSON {

	// ------------------------------------- Static fields -------------------------------------

	/**
	 * The FastJSON class.
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
				toJSONMethod = JSON_CLASS.getMethod("toJSONString", new Class[] {
					Object.class
				});
				toObjectMethod = JSON_CLASS.getMethod("parseObject", new Class[] {
					String.class,
					Class.class
				});
				Class<?> features = Class.forName("[Lcom.alibaba.fastjson.parser.Feature;");
				toObjectMethodByType = JSON_CLASS.getMethod("parseObject", new Class[] {
					String.class,
					Type.class,
					features
				});
				toArrayMethod = JSON_CLASS.getMethod("parseArray", new Class[] {
					String.class
				});
				Class<?> clazz = Class.forName("com.alibaba.fastjson.JSONArray");
				getArrayObjectMethod = clazz.getMethod("getObject", new Class[] {
					int.class,
					Class.class
				});
				getArrayObjectMethodByType = clazz.getMethod("getObject", new Class[] {
					int.class,
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
			return Class.forName("com.alibaba.fastjson.JSON");
		} catch (Exception e) {
			return null;
		}
	}

	// ------------------------------------- Constructor -------------------------------------

	/**
	 * Constructor for FastJSON converter.
	 */
	public FastJSON() {
	}

	// ------------------------------------- Override methods -------------------------------------

	@Override
	public void config(PropertiesMap<Object> data) {
	}

	@Override
	public String toJSONString(Object obj) throws Exception {
		if (obj == null) return null;
		return (String) toJSONMethod.invoke(null, obj);
	}

	@Override
	public <T> T toObject(Class<T> clazz, String str) throws Exception {
		if (str == null) return null;
		@SuppressWarnings("unchecked")
		T obj = (T) toObjectMethod.invoke(null, str, clazz);
		return obj;
	}

	@Override
	public <T> T toObject(Type type, String str) throws Exception {
		if (str == null) return null;
		@SuppressWarnings("unchecked")
		T obj = (T) toObjectMethodByType.invoke(null, str, type, null);
		return obj;
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Class<?>[] classes, String str) throws Exception {
		if (classes == null || classes.length == 0 || StringHelper.isEmpty(str)) return null;
		Object jsonArray = toArrayMethod.invoke(null, str);
		@SuppressWarnings("unchecked")
		T[] rets = (T[]) Array.newInstance(arrayClass, classes.length);
		for (int i = 0, c = rets.length; i < c; i++) {
			@SuppressWarnings("unchecked")
			T obj = (T) getArrayObjectMethod.invoke(jsonArray, i, classes[i]);
			rets[i] = obj;
		}
		return rets;
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Type[] types, String str) throws Exception {
		if (types == null || types.length == 0 || StringHelper.isEmpty(str)) return null;
		Object jsonArray = toArrayMethod.invoke(null, str);
		@SuppressWarnings("unchecked")
		T[] rets = (T[]) Array.newInstance(arrayClass, types.length);
		for (int i = 0, c = rets.length; i < c; i++) {
			@SuppressWarnings("unchecked")
			T obj = (T) getArrayObjectMethodByType.invoke(jsonArray, i, types[i]);
			rets[i] = obj;
		}
		return rets;
	}

}
