package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetInboundHandler;

/**
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetKafkaInbound<K, V, DATA extends NetData> extends NetInboundHandler<NetKafkaIO<K, V>, DATA> {

}
