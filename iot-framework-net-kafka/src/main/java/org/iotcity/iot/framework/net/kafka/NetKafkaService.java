package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.NetManager;
import org.iotcity.iot.framework.net.channel.NetChannel;
import org.iotcity.iot.framework.net.channel.NetServiceHandler;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigChannel;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigConsumer;
import org.iotcity.iot.framework.net.kafka.config.NetKafkaConfigProducer;
import org.iotcity.iot.framework.net.kafka.event.NetKafkaEventFactory;

/**
 * The kafka service for message consumer and producer.
 * @author ardon
 * @date 2021-06-20
 */
public final class NetKafkaService extends NetServiceHandler {

	/**
	 * The kafka event factory object.
	 */
	private final NetEventFactory eventFactory = new NetKafkaEventFactory();

	/**
	 * Constructor for kafka service.
	 * @param manager The net manager object (required, can not be null).
	 * @param serviceID The service unique identification (required, can not be or empty).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" or "serviceID" is null or empty.
	 */
	public NetKafkaService(NetManager manager, String serviceID) throws IllegalArgumentException {
		super(manager, serviceID);
	}

	@Override
	public final NetEventFactory getEventFactory() {
		return eventFactory;
	}

	@Override
	protected final boolean doConfig(PropertiesConfigFile file, boolean reset) {
		if (file == null) return true;

		// Load channel configuration.
		NetKafkaConfigChannel[] channels = PropertiesLoader.loadConfigArray(NetKafkaConfigChannel.class, file, "iot.framework.net.kafka");
		if (channels == null || channels.length == 0) return true;

		// Traverse all configurations.
		for (NetKafkaConfigChannel config : channels) {
			// Verify config data.
			if (!config.enabled || config.instance == null) continue;

			// New channel instance.
			Class<?> clazz = config.instance;
			NetChannel channel = null;
			try {
				// NetServiceHandler, String, NetKafkaConfigConsumer, NetKafkaConfigProducer
				channel = IoTFramework.getInstance(clazz, new Class<?>[] {
					NetServiceHandler.class,
					String.class,
					NetKafkaConfigConsumer.class,
					NetKafkaConfigProducer.class
				}, new Object[] {
					this,
					config.channelID,
					config.consumer,
					config.producer
				});
			} catch (NoSuchMethodException e) {
				try {
					channel = IoTFramework.getInstance(clazz);
				} catch (Exception e2) {
					FrameworkNet.getLogger().error(e2);
				}
			} catch (Exception e) {
				FrameworkNet.getLogger().error(e);
			}

			// Configure this channel.
			if (channel != null && channel.config(config.options, reset)) {
				// Add client to service.
				this.addClient(channel);
			} else {
				// Logs error message.
				FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.service.client.config.err", clazz.getName(), config.channelID, serviceID));
				// Return false.
				return false;
			}

		}
		// Return true.
		return true;
	}

	@Override
	protected final boolean doStart() throws Exception {
		return true;
	}

	@Override
	protected final boolean doStop() throws Exception {
		return true;
	}

}
