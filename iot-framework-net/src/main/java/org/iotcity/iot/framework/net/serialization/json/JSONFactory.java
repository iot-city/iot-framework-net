package org.iotcity.iot.framework.net.serialization.json;

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
	 * The locker for JSON class.
	 */
	private static final Object classLock = new Object();
	/**
	 * JSON converter class.
	 */
	private static Class<?> jsonClass;
	/**
	 * The locker for instance.
	 */
	private static final Object instanceLock = new Object();
	/**
	 * JSON default converter object.
	 */
	private static JSON instance;

	/**
	 * Set the default JSON converter class (the default JSON converter class will be replaced by this new one).
	 * @param clazz The JSON converter class.
	 */
	public static final void setDefaultJSONClass(Class<JSON> clazz) {
		if (clazz == null) return;
		synchronized (classLock) {
			jsonClass = clazz;
		}
	}

	/**
	 * Gets the default supported JSON class (never null).
	 */
	public static final Class<?> getDefaultJSONClass() {
		if (jsonClass != null) return jsonClass;
		synchronized (classLock) {
			if (jsonClass != null) return jsonClass;

			if (FastJSON.JSON_CLASS != null) {
				jsonClass = FastJSON.class;
			} else if (GsonJSON.JSON_CLASS != null) {
				jsonClass = GsonJSON.class;
			} else {
				jsonClass = NoneJSON.class;
			}

		}
		return jsonClass;
	}

	/**
	 * Set the default JSON converter instance (the default JSON converter instance will be replaced by this new one).
	 * @param json JSON converter instance (required, not null).
	 */
	public static final void setDefaultJSON(JSON json) {
		if (json == null) return;
		synchronized (instanceLock) {
			instance = json;
		}
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
	 * New a JSON converter instance by default JSON class (returns not null).
	 * @return JSON converter object.
	 */
	public static final JSON newJSON() {
		try {
			return (JSON) getDefaultJSONClass().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return new NoneJSON();
		}
	}

}
