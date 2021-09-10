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
 * The JSON converter for FastJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class FastJSON implements JSON {

	/**
	 * The framework logger.
	 */
	private final Logger logger = FrameworkNet.getLogger();

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
		if (JSONFactory.getJSONClass().equals(FastJSON.class)) {
			try {
				// Gets JSON methods.
				Class<?> clazz = Class.forName("com.alibaba.fastjson.JSON");
				toJSONMethod = clazz.getMethod("toJSONString", new Class[] {
					Object.class
				});
				toObjectMethod = clazz.getMethod("parseObject", new Class[] {
					String.class,
					Class.class
				});
				Class<?> features = Class.forName("[Lcom.alibaba.fastjson.parser.Feature;");
				toObjectMethodByType = clazz.getMethod("parseObject", new Class[] {
					String.class,
					Type.class,
					features
				});
				toArrayMethod = clazz.getMethod("parseArray", new Class[] {
					String.class
				});
				clazz = Class.forName("com.alibaba.fastjson.JSONArray");
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
	 * Constructor for FastJSON converter.
	 */
	public FastJSON() {
	}

	@Override
	public void config(PropertiesMap<Object> data) {
	}

	@Override
	public String toJSONString(Object obj) {
		try {
			return (String) toJSONMethod.invoke(null, new Object[] {
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
			T obj = (T) toObjectMethod.invoke(null, new Object[] {
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
		try {
			@SuppressWarnings("unchecked")
			T obj = (T) toObjectMethodByType.invoke(null, new Object[] {
				str,
				type,
				null
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
		try {
			Object jsonArray = toArrayMethod.invoke(null, str);
			@SuppressWarnings("unchecked")
			T[] rets = (T[]) Array.newInstance(arrayClass, classes.length);
			for (int i = 0, c = rets.length; i < c; i++) {
				@SuppressWarnings("unchecked")
				T obj = (T) getArrayObjectMethod.invoke(jsonArray, i, classes[i]);
				rets[i] = obj;
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
		try {
			Object jsonArray = toArrayMethod.invoke(null, str);
			@SuppressWarnings("unchecked")
			T[] rets = (T[]) Array.newInstance(arrayClass, types.length);
			for (int i = 0, c = rets.length; i < c; i++) {
				@SuppressWarnings("unchecked")
				T obj = (T) getArrayObjectMethodByType.invoke(jsonArray, i, types[i]);
				rets[i] = obj;
			}
			return rets;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

}
