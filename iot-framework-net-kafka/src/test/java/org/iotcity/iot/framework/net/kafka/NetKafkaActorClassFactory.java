package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.kafka.data.ClassA;
import org.iotcity.iot.framework.net.kafka.data.ClassB;
import org.iotcity.iot.framework.net.support.actor.NetActorClassFactory;
import org.iotcity.iot.framework.net.support.actor.NetActorCommand;

/**
 * @author ardon
 * @date 2021-09-12
 */
public class NetKafkaActorClassFactory implements NetActorClassFactory {

	@Override
	public Class<?>[] getParameterTypes(NetActorCommand command) {
		return new Class<?>[] {
			ClassA.class,
			ClassB.class
		};
	}

	@Override
	public Class<?> getReturnType(NetActorCommand command) {
		return ClassB.class;
	}

}
