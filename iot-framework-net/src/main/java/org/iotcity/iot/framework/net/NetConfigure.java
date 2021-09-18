package org.iotcity.iot.framework.net;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.net.config.NetConfig;
import org.iotcity.iot.framework.net.serialization.bytes.BYTESFactory;
import org.iotcity.iot.framework.net.serialization.json.JSONFactory;

/**
 * @author ardon
 * @date 2021-08-03
 */
public final class NetConfigure extends PropertiesConfigure<NetConfig> {

	@Override
	public final String getPrefixKey() {
		return "iot.framework.net";
	}

	@Override
	public final PropertiesConfigFile getDefaultExternalFile() {
		return new PropertiesConfigFile("framework-net.properties", "UTF-8", false);
	}

	@Override
	public final boolean config(Configurable<NetConfig> configurable, boolean reset) {
		// Verify configurable object class
		if (configurable == null || props == null) return false;
		// Get net configuration data.
		NetConfig config = PropertiesLoader.getConfigBean(NetConfig.class, props, getPrefixKey());
		// Check configuration.
		if (config == null) return true;
		// Set default configuration for serialization.
		JSONFactory.setDefaultConfiguration(config.serialization);
		BYTESFactory.setDefaultConfiguration(config.serialization);
		// Do manager configuration.
		return configurable.config(config, reset);
	}

}
