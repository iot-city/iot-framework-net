package org.iotcity.iot.framework.net.serialization.json;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.serialization.json.data.ClassA;
import org.iotcity.iot.framework.net.serialization.json.data.ClassB;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-06
 */
public class JSONTest extends TestCase {

	public void testJSON() {

		JavaHelper.log("----------------------------- TEST JSON FACTORY -----------------------------");

		JSON json = JSONFactory.getDefaultJSON();
		JavaHelper.log("JSON instance calss: " + json.getClass().getName());

		ClassA a = new ClassA("A", "A-DECS");
		ClassB b = new ClassB(1, "B", "B-DESC");

		JavaHelper.log("----------------------------- TEST JSON OBJECT -----------------------------");

		String str = json.toJSONString(a);
		JavaHelper.log("Object to JSON string A: " + str);
		ClassA aobj = json.toJavaObject(ClassA.class, str);
		JavaHelper.log("JSON string to Object A: " + aobj);

		JavaHelper.log("----------------------------- TEST JSON ARRAY SIMPLE -----------------------------");

		str = json.toJSONString(new ClassA[] {
			new ClassA("A1", "A1-DECS"),
			new ClassA("A2", "A2-DECS")
		});
		JavaHelper.log("Array to JSON string: " + str);
		ClassA[] array1 = json.toJavaObject(ClassA[].class, str);
		if (array1 != null) {
			JavaHelper.log("JSON string to Array: " + array1.length + "; A1: " + array1[0] + "; A2: " + array1[1]);
		} else {
			JavaHelper.log("JSON string to Array: null");
		}

		JavaHelper.log("----------------------------- TEST JSON ARRAY MULTIPLE -----------------------------");

		str = json.toJSONString(new Serializable[] {
			a,
			b
		});
		JavaHelper.log("Array to JSON string: " + str);
		Serializable[] array2 = json.toJavaArray(Serializable.class, new Class<?>[] {
			ClassA.class,
			ClassB.class
		}, str);
		if (array2 != null) {
			JavaHelper.log("JSON string to Array: " + array2.length + "; A: " + array2[0] + "; B: " + array2[1]);
		} else {
			JavaHelper.log("JSON string to Array: null");
		}

		JavaHelper.log("----------------------------- TEST JSON FACTORY COMPLETE -----------------------------");

	}

}
