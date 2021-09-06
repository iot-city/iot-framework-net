package org.iotcity.iot.framework.net.serialization.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.iotcity.iot.framework.net.serialization.json.impls.FastJSON;
import org.iotcity.iot.framework.net.serialization.json.impls.GsonJSON;
import org.iotcity.iot.framework.net.serialization.json.impls.NoneJSON;

/**
 * The JSON converter object factory.
 * @author ardon
 * @date 2021-09-06
 */
public final class JSONFactory {

	/**
	 * JSON converter class.
	 */
	private static Class<?> jsonClass;
	/**
	 * JSON default converter object.
	 */
	private static JSON instance;
	/**
	 * The instance lock.
	 */
	private static final Object instanceLock = new Object();

	static {
		// Initialize all supported JSON classes.
		Map<String, Class<?>> jsons = new LinkedHashMap<>();
		jsons.put("com.alibaba.fastjson.JSON", FastJSON.class);
		jsons.put("com.google.gson.Gson", GsonJSON.class);
		// Traversal the map.
		for (Entry<String, Class<?>> kv : jsons.entrySet()) {
			try {
				if (Class.forName(kv.getKey()) != null) {
					jsonClass = kv.getValue();
					break;
				}
			} catch (Exception e) {
			}
		}
		// Set to none JSON class.
		if (jsonClass == null) jsonClass = NoneJSON.class;
	}

	/**
	 * Gets the default JSON converter object (returns not null).
	 */
	public static final JSON getDefaultJSON() {
		if (instance != null) return instance;
		synchronized (instanceLock) {
			if (instance != null) return instance;
			instance = newJSON();
		}
		return instance;
	}

	/**
	 * New a JSON converter instance (returns not null).
	 * @return JSON converter object.
	 */
	public static final JSON newJSON() {
		try {
			return (JSON) jsonClass.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return new NoneJSON();
		}
	}

}
