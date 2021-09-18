package org.iotcity.iot.framework.net.kafka;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetChannelState;
import org.iotcity.iot.framework.net.io.NetResponseCallback;
import org.iotcity.iot.framework.net.io.NetResponseResult;
import org.iotcity.iot.framework.net.kafka.data.ClassA;
import org.iotcity.iot.framework.net.kafka.data.ClassB;
import org.iotcity.iot.framework.net.kafka.io.NetKafkaTopicPartition;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequest;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorRequestData;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponse;
import org.iotcity.iot.framework.net.kafka.support.actor.NetKafkaActorResponseData;
import org.iotcity.iot.framework.net.kafka.support.bus.NetKafkaChannelEvent;
import org.iotcity.iot.framework.net.serialization.bytes.BYTESFactory;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;
import org.iotcity.iot.framework.net.support.actor.NetActorHeader;
import org.iotcity.iot.framework.net.support.actor.NetActorResult;
import org.iotcity.iot.framework.net.support.bus.NetChannelEventData;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-10
 */
public class NetKafkaTest extends TestCase {

	private Object lock = new Object();
	private Logger logger = FrameworkNet.getLogger();
	private static AtomicInteger index = new AtomicInteger();
	private static AtomicInteger returned = new AtomicInteger();
	private static final int MAX_SEND = 20000;

	public void testKafka() {

		JavaHelper.log("----------------------------- TEST KAFKA -----------------------------");

		// Add the producer listener and sent test data.
		addProducerListener();

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

	/**
	 * Add the producer listener and sent test data.
	 */
	private void addProducerListener() {
		// Add channel event listener.
		IoTFramework.getBusEventPublisher().addListener(NetChannelEventData.class, new BusEventListener() {

			@Override
			public boolean onEvent(BusEvent event) throws Exception {

				NetChannelEventData data = event.getEventData();
				if (data.getState() != NetChannelState.OPENED) return false;

				BYTESFactory.getDefaultBytes().register(new Class<?>[] {
					ClassA.class,
					ClassB.class,
					Serializable.class,
					NetKafkaTopicPartition.class,
					NetActorHeader.class,
					NetActorCommand.class,
					NetKafkaTopicPartition.class,
					NetActorResult.class,
					NetKafkaActorRequestData.class,
					NetKafkaActorResponseData.class
				});

				TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

					@Override
					public void run() {
						// Get kafka client channel.
						sendToKafka(data.getChannel());
					}

				}, 1000);

				return true;
			}

		}, 0, NetKafkaChannelEvent.class);

	}

	private void sendToKafka(NetChannel client) {

		NetManager manager = client.getService().getNetManager();

		final long startSend = System.currentTimeMillis();
		final AtomicLong startRecv = new AtomicLong();
		logger.warn("SENDING TOTAL OF REQS: " + MAX_SEND);

		for (int i = 0, c = MAX_SEND; i < c; i++) {

			int idx = index.incrementAndGet();
			// Create parameters.
			NetKafkaTopicPartition partition = new NetKafkaTopicPartition("APP-REQ-DEMO1", null);
			NetActorHeader header = new NetActorHeader(null, null);
			NetActorCommand command = new NetActorCommand("DEMO1", "1.0.0", "module1", "page1", "command1");
			Serializable[] params = new Serializable[] {
				new ClassA("A", "A-DECS"),
				new ClassB(idx, "B", "B-DESC")
			};
			NetKafkaActorRequest req = new NetKafkaActorRequest(StringHelper.getUUID(), partition, header, command, params);

			// Send to remote.
			boolean success = manager.asyncRequestOne(client, req, NetKafkaActorResponse.class, new NetResponseCallback<NetKafkaActorResponse>() {

				@Override
				public void onCallback(NetResponseResult<NetKafkaActorResponse> result) throws Exception {

					int recv = returned.incrementAndGet();
					if (recv == 1) {
						startRecv.set(System.currentTimeMillis());
					}
					if (recv == MAX_SEND) {
						logger.warn("CALLBACK FINISHED: " + ConvertHelper.formatMilliseconds(System.currentTimeMillis() - startRecv.get()));
						logger.warn("RESULT: " + result);
						// Notify to complete.
						synchronized (lock) {
							lock.notify();
						}
					} else if (recv % 1000 == 0) {
						logger.info("RES-" + recv + ": " + result);
					}

				}

			}, 0);

			if (!success) logger.error("REQ" + idx + ": " + success);
		}

		logger.warn("SENT REQ FINISHED: " + ConvertHelper.formatMilliseconds(System.currentTimeMillis() - startSend));

	}

}
