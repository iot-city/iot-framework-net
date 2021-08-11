package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetOutboundHandler;

/**
 * Kafka message outbound object class for data processing.
 * @param <K> The kafka message key type.
 * @param <K> The kafka message value type.
 * @param <DATA> The outbound message data object type.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetKafkaOutbound<K, V, DATA extends NetData> extends NetOutboundHandler<NetKafkaIO<K, V>, DATA> {

}
