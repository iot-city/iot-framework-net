package org.iotcity.iot.framework.net.serialization.json.impls;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.net.serialization.json.JSON;

/**
 * The JSON converter for NoneJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class NoneJSON implements JSON {

	@Override
	public void config(PropertiesMap<Object> data) {
	}

	@Override
	public String toJSONString(Object obj) {
		return null;
	}

	@Override
	public <T> T toJavaObject(Class<T> clazz, String json) {
		return null;
	}

	@Override
	public <T> T[] toJavaArray(Class<T> arrayClass, Class<?>[] classes, String json) {
		return null;
	}

}
