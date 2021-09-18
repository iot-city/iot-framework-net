package org.iotcity.iot.framework.net.serialization.json;

import org.iotcity.iot.framework.net.config.NetConfigSerialization;
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
	 * The global configuration data for converters.
	 */
	private static final NetConfigSerialization config = new NetConfigSerialization();
	/**
	 * The locker for JSON.
	 */
	private static final Object lock = new Object();
	/**
	 * JSON converter class.
	 */
	private static Class<?> jsonClass;
	/**
	 * JSON default converter object.
	 */
	private static JSON instance;

	/**
	 * Set the default configuration data for default JSON converter.
	 * @param data The configuration data.
	 */
	public static final void setDefaultConfiguration(NetConfigSerialization data) {
		if (data == null) return;
		config.capacity = data.capacity;
		config.dateFormat = data.dateFormat;
		JSON obj = instance;
		if (obj != null) obj.config(config, false);
	}

	/**
	 * Set the default JSON converter class (the default JSON converter class will be replaced by this new one).
	 * @param clazz The JSON converter class.
	 */
	public static final void setDefaultJSONClass(Class<JSON> clazz) {
		if (clazz == null || jsonClass == clazz) return;
		synchronized (lock) {
			jsonClass = clazz;
			instance = null;
		}
	}

	/**
	 * Gets the default supported JSON class (never null).
	 */
	public static final Class<?> getDefaultJSONClass() {
		if (jsonClass != null) return jsonClass;
		synchronized (lock) {
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
		synchronized (lock) {
			instance = json;
		}
	}

	/**
	 * Gets the default JSON converter object (returns not null).
	 */
	public static final JSON getDefaultJSON() {
		if (instance != null) return instance;
		synchronized (lock) {
			if (instance != null) return instance;
			instance = newJSON();
			instance.config(config, false);
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
