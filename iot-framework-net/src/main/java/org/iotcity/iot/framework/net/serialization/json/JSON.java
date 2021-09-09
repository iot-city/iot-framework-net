package org.iotcity.iot.framework.net.serialization.json;

import java.lang.reflect.Type;

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
	 * 
	 * <pre>
	 * 
	 * JSON json = JSONFactory.getDefaultJSON();
	 * String str = json.toJSONString(new Model(1001, "Jobs"));
	 * </pre>
	 * 
	 * @param obj Object that need to be converted.
	 * @return JSON string.
	 */
	String toJSONString(Object obj);

	/**
	 * Converts a JSON string to an object.
	 * 
	 * <pre>
	 * 
	 * String str = "{\"id\":1001,\"name\":\"Jobs\"}";
	 * JSON json = JSONFactory.getDefaultJSON();
	 * Model model = json.toJavaObject(Model.class, str);
	 * </pre>
	 * 
	 * @param <T> The object type.
	 * @param clazz The object class.
	 * @param str The JSON string to be converted.
	 * @return An object from JSON string.
	 */
	<T> T toJavaObject(Class<T> clazz, String str);

	/**
	 * Converts a JSON string to an object.
	 * 
	 * <pre>
	 * 
	 * String str = "[{\"id\":1001,\"name\":\"Jobs\"}]";
	 * JSON json = JSONFactory.getDefaultJSON();
	 * 
	 * List&lt;Model&gt; models = json.toJavaObject(new JSONTypeReference&lt;List&lt;Model&gt;&gt;() {
	 * }.getType(), str);
	 * 
	 * </pre>
	 * 
	 * @param <T> The object type.
	 * @param type Type reference.
	 * @param str The JSON string to be converted.
	 * @return An object from JSON string.
	 */
	<T> T toJavaObject(Type type, String str);

	/**
	 * Converts a JSON string to a specified classes array.
	 * 
	 * <pre>
	 * 
	 * String str = "[{\"a\":1001}, {\"b\":\"Jobs\"}]";
	 * JSON json = JSONFactory.getDefaultJSON();
	 * 
	 * Serializable[] models = json.toJavaArray(Serializable.class, new Class<?>[] {
	 * 	ModelA.class,
	 * 	ModelB.class
	 * }, str);
	 * 
	 * </pre>
	 * 
	 * @param <T> The array type.
	 * @param arrayClass The array class.
	 * @param classes The object classes in array.
	 * @param str The JSON string to be converted.
	 * @return An array from JSON string.
	 */
	<T> T[] toJavaArray(Class<T> arrayClass, Class<?>[] classes, String str);

	/**
	 * Converts a JSON string to a specified references array.
	 * 
	 * <pre>
	 * 
	 * String str = "[[{\"a\":1001}], [{\"b\":\"Jobs\"}]]";
	 * JSON json = JSONFactory.getDefaultJSON();
	 * 
	 * Serializable[] models = json.toJavaArray(Serializable.class, new Type[] {
	 * 	new JSONTypeReference&lt;List&lt;Model&gt;&gt;() {
	 * 	}.getType(),
	 * 	new JSONTypeReference&lt;List&lt;Model&gt;&gt;() {
	 * 	}.getType()
	 * }, str);
	 * 
	 * </pre>
	 * 
	 * @param <T> The array type.
	 * @param arrayClass The array class.
	 * @param types Type references in array.
	 * @param str The JSON string to be converted.
	 * @return An array from JSON string.
	 */
	<T> T[] toJavaArray(Class<T> arrayClass, Type[] types, String str);

}
