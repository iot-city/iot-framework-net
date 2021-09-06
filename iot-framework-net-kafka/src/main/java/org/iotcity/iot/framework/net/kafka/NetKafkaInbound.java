package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;

/**
 * Kafka message inbound object class for data processing.
 * @param <K> The kafka message key type.
 * @param <V> The kafka message value type.
 * @param <DATA> The inbound message data object type.
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetKafkaInbound<K, V, DATA extends NetData> extends NetInboundHandler<NetKafkaIO<K, V>, DATA> {

}
