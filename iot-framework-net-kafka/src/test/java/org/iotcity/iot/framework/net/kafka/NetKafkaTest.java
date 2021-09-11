package org.iotcity.iot.framework.net.kafka;

import java.io.Serializable;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.io.NetResponseCallback;
import org.iotcity.iot.framework.net.io.NetResponseResult;
import org.iotcity.iot.framework.net.kafka.data.ClassA;
import org.iotcity.iot.framework.net.kafka.data.ClassB;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.bus.NetKafkaChannelEvent;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;
import org.iotcity.iot.framework.net.support.actor.NetActorHeader;
import org.iotcity.iot.framework.net.support.bus.NetChannelEventData;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-10
 */
public class NetKafkaTest extends TestCase {

	private Object lock = new Object();
	private Logger logger = FrameworkNet.getLogger();

	public void testKafka() {

		JavaHelper.log("----------------------------- TEST KAFKA -----------------------------");

		// Add channel event listener.
		IoTFramework.getBusEventPublisher().addListener(NetChannelEventData.class, new BusEventListener() {

			@Override
			public boolean onEvent(BusEvent event) throws Exception {

				NetChannelEventData data = event.getEventData();
				if (data.getState() != NetChannelState.OPENED) return false;

				// Get kafka client channel.
				NetChannel client = data.getChannel();
				NetManager manager = client.getService().getNetManager();

				// Create parameters.
				NetKafkaTopicPartition partition = new NetKafkaTopicPartition("APP-REQ-DEOM1", null);
				NetActorHeader header = new NetActorHeader(null, null);
				NetActorCommand command = new NetActorCommand("DEMO1", "1.0.0", "module1", "page1", "command1");
				Serializable[] params = new Serializable[] {
					new ClassA("A", "A-DECS"),
					new ClassB(1, "B", "B-DESC")
				};
				NetKafkaActorRequest req = new NetKafkaActorRequest(StringHelper.getUUID(), partition, header, command, params);

				// Send to remote.
				boolean success = manager.asyncRequestOne(client, req, NetKafkaActorResponse.class, new NetResponseCallback<NetKafkaActorResponse>() {

					@Override
					public void onCallback(NetResponseResult<NetKafkaActorResponse> result) throws Exception {
						logger.info("RES: " + result);
					}

				}, 0);

				logger.info("REQ: " + success);

				return true;
			}

		}, 0, NetKafkaChannelEvent.class);

		// Init the framework.
		IoTFramework.init();

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
