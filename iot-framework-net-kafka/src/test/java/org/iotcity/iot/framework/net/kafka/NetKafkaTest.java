package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-10
 */
public class NetKafkaTest extends TestCase {

	private Object lock = new Object();

	public void testKafka() {

		IoTFramework.init();

		JavaHelper.log("----------------------------- TEST KAFKA -----------------------------");

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		JavaHelper.log("----------------------------- TEST KAFKA COMPLETE -----------------------------");

	}

}
