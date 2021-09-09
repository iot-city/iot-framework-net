package org.iotcity.iot.framework.net.serialization.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a generic type {@code T}.
 * <p>
 * For example, to create a type literal for {@code List<String>}, you can create an empty anonymous inner class:
 *
 * <pre>
 * 
 * Type type = new JSONTypeReference&lt;List&lt;String&gt;&gt;() {
 * }.getType();
 * </pre>
 * 
 * @param <T> The class type to get.<br/>
 * @author ardon
 * @date 2021-09-09
 */
public class JSONTypeReference<T> {

	/**
	 * The type caches.
	 */
	private static final ConcurrentMap<Type, Type> caches = new ConcurrentHashMap<>();
	/**
	 * Current object type.
	 */
	private final Type type;

	/**
	 * Constructs a new type literal. Derives represented class from type parameter.
	 */
	protected JSONTypeReference() {
		Type superClass = getClass().getGenericSuperclass();
		Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
		Type cachedType = caches.get(type);
		if (cachedType == null) {
			caches.putIfAbsent(type, type);
			cachedType = caches.get(type);
		}
		this.type = cachedType;
	}

	/**
	 * Gets the class type {@code Type} instance.
	 */
	public Type getType() {
		return type;
	}

}
