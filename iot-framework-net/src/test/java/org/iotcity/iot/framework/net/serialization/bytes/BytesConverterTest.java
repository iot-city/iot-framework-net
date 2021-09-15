package org.iotcity.iot.framework.net.serialization.bytes;

import java.io.Serializable;
import java.util.HashMap;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.serialization.bytes.impls.FSTBytes;
import org.iotcity.iot.framework.net.serialization.bytes.impls.JavaBytes;
import org.iotcity.iot.framework.net.serialization.bytes.impls.KryoBytes;
import org.iotcity.iot.framework.net.serialization.json.data.ClassA;
import org.iotcity.iot.framework.net.serialization.json.data.ClassB;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-15
 */
public class BytesConverterTest extends TestCase {

	public void testSerializable() {

		JavaHelper.log("----------------------------- TEST SERIALIZABLE SERIALIZATION -----------------------------");

		HashMap<String, ClassA> map = new HashMap<>();
		map.put("C", new ClassA("C", "C-DECS"));
		map.put("D", new ClassA("D", "D-DECS"));
		Serializable[] array = new Serializable[] {
			new ClassA("A", "A-DECS"),
			new ClassB(1, "B", "B-DESC"),
			map
		};

		JavaHelper.log("----------------------------- TEST JAVA SERIALIZATION -----------------------------");

		JavaBytes javaBytes = new JavaBytes();

		try {
			byte[] bs = javaBytes.serialize(array);
			JavaHelper.log("JavaBytes array length: " + bs.length);
			Serializable[] cdata = javaBytes.deserialize(bs);
			if (cdata != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, ClassA> cmap = (HashMap<String, ClassA>) cdata[2];
				JavaHelper.log("JavaBytes result: [A: " + cdata[0] + ", B: " + cdata[1] + ", C: " + cmap.get("C") + "]");
			} else {
				JavaHelper.log("JavaBytes result: null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (FSTBytes.FST_CLASS != null) {

			JavaHelper.log("----------------------------- TEST FST SERIALIZATION -----------------------------");

			FSTBytes fstBytes = new FSTBytes();

			try {
				fstBytes.register(ClassA.class, ClassB.class, Serializable[].class);
				byte[] bs = fstBytes.serialize(array);
				JavaHelper.log("FSTBytes array length: " + bs.length);
				Serializable[] cdata = fstBytes.deserialize(bs);
				if (cdata != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, ClassA> cmap = (HashMap<String, ClassA>) cdata[2];
					JavaHelper.log("FSTBytes result: [A: " + cdata[0] + ", B: " + cdata[1] + ", C: " + cmap.get("C") + "]");
				} else {
					JavaHelper.log("FSTBytes result: null");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (KryoBytes.KRYO_CLASS != null) {

			JavaHelper.log("----------------------------- TEST KRYO SERIALIZATION -----------------------------");

			KryoBytes kryoBytes = new KryoBytes();

			try {
				kryoBytes.register(ClassA.class, ClassB.class, Serializable[].class);
				byte[] bs = kryoBytes.serialize(array);
				JavaHelper.log("KryoBytes array length: " + bs.length);
				Serializable[] cdata = kryoBytes.deserialize(bs);
				if (cdata != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, ClassA> cmap = (HashMap<String, ClassA>) cdata[2];
					JavaHelper.log("KryoBytes result: [A: " + cdata[0] + ", B: " + cdata[1] + ", C: " + cmap.get("C") + "]");
				} else {
					JavaHelper.log("KryoBytes result: null");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JavaHelper.log("----------------------------- TEST FACTORY SERIALIZATION -----------------------------");

		BYTES bytes = BYTESFactory.getDefaultBytes();
		JavaHelper.log("FACTORY instance calss: " + bytes.getClass().getName());
		try {
			bytes.register(ClassA.class, ClassB.class, Serializable[].class);
			byte[] bs = bytes.serialize(array);
			JavaHelper.log("FACTORY array length: " + bs.length);
			Serializable[] cdata = bytes.deserialize(bs);
			if (cdata != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, ClassA> cmap = (HashMap<String, ClassA>) cdata[2];
				JavaHelper.log("FACTORY result: [A: " + cdata[0] + ", B: " + cdata[1] + ", C: " + cmap.get("C") + "]");
			} else {
				JavaHelper.log("FACTORY result: null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JavaHelper.log("----------------------------- TEST SERIALIZATION COMPLETE -----------------------------");

	}

}
