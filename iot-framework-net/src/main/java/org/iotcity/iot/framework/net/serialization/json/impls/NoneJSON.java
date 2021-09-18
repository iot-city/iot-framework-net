package org.iotcity.iot.framework.net.serialization.json.impls;

import java.lang.reflect.Type;

import org.iotcity.iot.framework.net.config.NetConfigSerialization;
import org.iotcity.iot.framework.net.serialization.json.JSON;

/**
 * The JSON converter for NoneJSON.
 * @author ardon
 * @date 2021-09-06
 */
public final class NoneJSON implements JSON {

	@Override
	public boolean config(NetConfigSerialization data, boolean reset) {
		return true;
	}

	@Override
	public String toJSONString(Object obj) throws Exception {
		throw new Exception("No JSON conversion instance.");
	}

	@Override
	public <T> T toObject(Class<T> clazz, String str) throws Exception {
		throw new Exception("No JSON conversion instance.");
	}

	@Override
	public <T> T toObject(Type type, String str) throws Exception {
		throw new Exception("No JSON conversion instance.");
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Class<?>[] classes, String str) throws Exception {
		throw new Exception("No JSON conversion instance.");
	}

	@Override
	public <T> T[] toArray(Class<T> arrayClass, Type[] types, String str) throws Exception {
		throw new Exception("No JSON conversion instance.");
	}

}
