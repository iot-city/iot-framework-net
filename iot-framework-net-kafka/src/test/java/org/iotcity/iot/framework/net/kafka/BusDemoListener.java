package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.core.bus.BusDataListener;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * @author ardon
 * @date 2021-09-19
 */
@BusDataListener(Object.class)
public class BusDemoListener implements BusEventListener {

	@Override
	public boolean onEvent(BusEvent event) throws Exception {
		JavaHelper.log(event.getClass().getSimpleName() + ": " + event);
		return false;
	}

}
