package org.iotcity.iot.framework.net.serialization.json;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * The JSON converter.
 * @author ardon
 * @date 2021-09-06
 */
public interface JSON {

	/**
	 * Configure the JSON converter object.
	 * @param data The configuration map.
	 */
	void config(PropertiesMap<Object> data);

	/**
	 * Converts an object to a JSON string.
	 * @param obj Object that need to be converted.
	 * @return JSON string.
	 */
	String toJSONString(Object obj);

	/**
	 * Converts a JSON string to an object.
	 * @param <T> The object type.
	 * @param clazz The object class.
	 * @param str The JSON string to be converted.
	 * @return An object from JSON string.
	 */
	<T> T toJavaObject(Class<T> clazz, String str);

	/**
	 * Converts a JSON string to a specified classes array.
	 * @param <T> The array type.
	 * @param arrayClass The array class.
	 * @param classes The object classes in array.
	 * @param str The JSON string to be converted.
	 * @return An array from JSON string.
	 */
	<T> T[] toJavaArray(Class<T> arrayClass, Class<?>[] classes, String str);

}
