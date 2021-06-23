package org.iotcity.iot.framework.net.kafka;

import org.iotcity.iot.framework.net.io.NetData;
import org.iotcity.iot.framework.net.io.NetOutboundHandler;

/**
 * @author ardon
 * @date 2021-06-16
 */
public abstract class NetKafkaOutbound<K, V, DATA extends NetData> extends NetOutboundHandler<NetKafkaIO<K, V>, DATA> {

}
