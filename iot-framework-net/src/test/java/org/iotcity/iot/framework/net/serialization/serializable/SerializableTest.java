package org.iotcity.iot.framework.net.serialization.serializable;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.serialization.json.data.ClassA;
import org.iotcity.iot.framework.net.serialization.json.data.ClassB;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-07
 */
public class SerializableTest extends TestCase {

	public void testSerializable() {

		JavaHelper.log("----------------------------- TEST SERIALIZABLE SERIALIZATION -----------------------------");

		Serializable[] array = new Serializable[] {
			new ClassA("A", "A-DECS"),
			new ClassB(1, "B", "B-DESC")
		};

		try {
			byte[] bs = SerializableHelper.serialize(array);
			JavaHelper.log("Data array length: " + bs.length);
			Serializable[] cdata = SerializableHelper.deserialize(bs);
			if (cdata != null) {
				JavaHelper.log("Deserialize result: [A: " + cdata[0] + ", B: " + cdata[1] + "]");
			} else {
				JavaHelper.log("Deserialize result: null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JavaHelper.log("----------------------------- TEST SERIALIZATION COMPLETE -----------------------------");

	}

}
