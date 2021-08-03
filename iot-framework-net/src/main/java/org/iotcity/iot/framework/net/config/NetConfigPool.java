package org.iotcity.iot.framework.net.config;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.iotcity.iot.framework.core.util.task.PriorityLimitedBlockingQueue;

/**
 * Thread pool executor configure data of net manager.
 * @author ardon
 * @date 2021-08-03
 */
public class NetConfigPool {

	/**
	 * The number of threads to keep in the pool (8 by default).
	 */
	public int corePoolSize = 8;
	/**
	 * The maximum number of threads to allow in the pool (8 by default).
	 */
	public int maximumPoolSize = 8;
	/**
	 * The maximum seconds that excess idle threads will wait for new tasks before terminating (60s by default).
	 */
	public long keepAliveTime = 60;
	/**
	 * The capacity of blocking queue to cache tasks in thread pool executor (1000 by default, when set to 0, the synchronous queue {@link SynchronousQueue } is used; when set to greater than 0, the bounded priority blocking queue {@link PriorityLimitedBlockingQueue } is used; when set to less than 0, the unbounded priority blocking queue {@link PriorityBlockingQueue } is used).
	 */
	public int capacity = 1000;

}
