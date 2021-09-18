package org.iotcity.iot.framework.net.serialization.bytes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.task.PriorityRunnable;
import org.iotcity.iot.framework.core.util.task.TaskHandler;
import org.iotcity.iot.framework.net.serialization.bytes.impls.KryoBytes;
import org.iotcity.iot.framework.net.serialization.json.data.ClassA;
import org.iotcity.iot.framework.net.serialization.json.data.ClassB;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-18
 */
public class KryoConverterTest extends TestCase {

	private static final int COUNT = 240000;
	private static final int THREADS = 4;
	private final Object lock = new Object();
	private final AtomicInteger counter = new AtomicInteger();
	private long totalStartTime;

	public void testSerializable() {

		JavaHelper.log("----------------------------- TEST KRYO SERIALIZATION -----------------------------");

		if (KryoBytes.KRYO_CLASS != null) {

			List<Serializable[]> list = new ArrayList<>(COUNT);
			for (int i = 0, c = COUNT; i < c; i++) {
				Serializable[] array = new Serializable[] {
					new ClassA("A", "A-DECS"),
					new ClassB(1, "B", "B-DESC")
				};
				list.add(array);
			}

			List<byte[]> bytes = new ArrayList<byte[]>(list.size());

			KryoBytes kryoBytes = new KryoBytes();
			kryoBytes.register(ClassA.class, ClassB.class, Serializable[].class);

			try {
				// Test one data.
				byte[] tbs = kryoBytes.serialize(list.get(0));
				JavaHelper.log("Bytes size: " + tbs.length);

				JavaHelper.log("RUN IN ONE THREAD...");
				// Run test.
				runKryoData(list, bytes, kryoBytes);

				// init thread pool.
				TaskHandler handler = TaskHandler.getDefaultHandler();
				for (int i = 0, c = 10; i < c; i++) {
					handler.run(new PriorityRunnable() {

						@Override
						public void run() {
						}

					});
				}

				synchronized (lock) {
					try {
						lock.wait(2000);
					} catch (Exception e) {
					}
				}

				JavaHelper.log("RUN IN MULTI THREADS...");
				int each = COUNT / THREADS;
				totalStartTime = System.currentTimeMillis();
				for (int i = 0; i < THREADS; i++) {
					final List<Serializable[]> listCopy = list.subList(i * each, each * (i + 1));
					final List<byte[]> bytesCopy = new ArrayList<byte[]>(each);
					handler.run(new PriorityRunnable() {

						@Override
						public void run() {
							try {
								runKryoData(listCopy, bytesCopy, kryoBytes);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});
				}

				synchronized (lock) {
					lock.wait();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JavaHelper.log("----------------------------- TEST KRYO COMPLETE -----------------------------");

	}

	private void runKryoData(List<Serializable[]> list, List<byte[]> bytes, KryoBytes kryoBytes) throws Exception {

		long startTime1 = System.currentTimeMillis();
		for (Serializable[] sers : list) {
			bytes.add(kryoBytes.serialize(sers));
		}
		long endTime1 = System.currentTimeMillis();

		JavaHelper.log("Serialize time: " + ConvertHelper.formatMilliseconds(endTime1 - startTime1));

		long startTime2 = System.currentTimeMillis();
		for (byte[] bs : bytes) {
			kryoBytes.deserialize(bs);
		}
		long endTime2 = System.currentTimeMillis();

		JavaHelper.log("Deserialize time: " + ConvertHelper.formatMilliseconds(endTime2 - startTime2));

		int total = counter.incrementAndGet();
		if (total == 1) {
			JavaHelper.log("Total single-threading time: " + ConvertHelper.formatMilliseconds(endTime2 - startTime1));
		} else if (total == THREADS + 1) {
			JavaHelper.log("Total multi-threading time: " + ConvertHelper.formatMilliseconds(endTime2 - totalStartTime));
			synchronized (lock) {
				lock.notify();
			}
		}

	}

}
