package org.iotcity.iot.framework.net;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.AutoConfigureManager;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigureManager;
import org.iotcity.iot.framework.core.i18n.LocaleConfigure;
import org.iotcity.iot.framework.core.logging.LoggerConfigure;

/**
 * Configure manager of framework net.
 * @author ardon
 * @date 2021-06-26
 */
@AutoConfigureManager
public final class NetConfigureManager extends PropertiesConfigureManager {

	/**
	 * Constructor for configure manager of framework net.
	 */
	public NetConfigureManager() {

		// For internal i18n locale configure
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/net/resources/i18n-net-config.properties";
		file.fromPackage = true;
		this.addInternal(new LocaleConfigure(), IoTFramework.getLocaleFactory(), file, false);

		// For internal logging configure
		file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/net/resources/logging-net-config.properties";
		file.fromPackage = true;
		this.addInternal(new LoggerConfigure(), IoTFramework.getLoggerFactory(), file, false);

		// For external network manager configuration.
		this.addExternal(new NetConfigure(), FrameworkNet.getGlobalNetManager(), false);

	}

	@Override
	protected void onPerformed() {
	}

}
