package org.iotcity.iot.framework.net.serialization.json;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.net.serialization.json.data.ClassA;
import org.iotcity.iot.framework.net.serialization.json.data.ClassB;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-09-06
 */
public class JSONTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testJSON() throws Exception {

		JavaHelper.log("----------------------------- TEST JSON FACTORY -----------------------------");

		JSON json = JSONFactory.getDefaultJSON();
		JavaHelper.log("JSON instance calss: " + json.getClass().getName());

		ClassA a = new ClassA("A", "A-DECS");
		ClassB b = new ClassB(1, "B", "B-DESC");

		JavaHelper.log("----------------------------- TEST JSON OBJECT -----------------------------");

		String str = json.toJSONString(a);
		JavaHelper.log("Object to JSON string A: " + str);
		ClassA aobj = json.toObject(ClassA.class, str);
		JavaHelper.log("JSON string to Object A: " + aobj);

		JavaHelper.log("----------------------------- TEST JSON OBJECT TYPE -----------------------------");

		List<ClassA> list = new ArrayList<>();
		list.add(new ClassA("A1", "A1-DECS"));
		list.add(new ClassA("A2", "A2-DECS"));
		str = json.toJSONString(list);
		JavaHelper.log("List to JSON string: " + str);
		list = json.toObject(new JSONTypeReference<List<ClassA>>() {
		}.getType(), str);
		if (list != null) {
			JavaHelper.log("JSON string to List: " + list.size() + "; A1: " + list.get(0) + "; A2: " + list.get(1));
		} else {
			JavaHelper.log("JSON string to List: null");
		}

		JavaHelper.log("----------------------------- TEST JSON ARRAY SIMPLE -----------------------------");

		str = json.toJSONString(new ClassA[] {
			new ClassA("A1", "A1-DECS"),
			new ClassA("A2", "A2-DECS")
		});
		JavaHelper.log("Array to JSON string: " + str);
		ClassA[] array1 = json.toObject(ClassA[].class, str);
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
		Serializable[] array2 = json.toArray(Serializable.class, new Class<?>[] {
			ClassA.class,
			ClassB.class
		}, str);
		if (array2 != null) {
			JavaHelper.log("JSON string to Array: " + array2.length + "; A: " + array2[0] + "; B: " + array2[1]);
		} else {
			JavaHelper.log("JSON string to Array: null");
		}

		JavaHelper.log("----------------------------- TEST JSON ARRAY MULTIPLE LIST -----------------------------");

		List<ClassA> listA = new ArrayList<>();
		listA.add(new ClassA("A1", "A1-DECS"));
		listA.add(new ClassA("A2", "A2-DECS"));
		List<ClassB> listB = new ArrayList<>();
		listB.add(new ClassB(1, "B1", "B1-DESC"));
		listB.add(new ClassB(2, "B2", "B2-DESC"));
		str = json.toJSONString(new Object[] {
			listA,
			listB
		});
		JavaHelper.log("Array list to JSON string: " + str);
		Object[] objects = json.toArray(Object.class, new Type[] {
			new JSONTypeReference<List<ClassA>>() {
			}.getType(),
			new JSONTypeReference<List<ClassB>>() {
			}.getType()
		}, str);
		if (objects != null) {
			JavaHelper.log("JSON string to Array list: " + objects.length + "; A: " + ((List<ClassA>) objects[0]).get(0) + "; B: " + ((List<ClassB>) objects[1]).get(0));
		} else {
			JavaHelper.log("JSON string to Array list: null");
		}

		JavaHelper.log("----------------------------- TEST JSON FACTORY COMPLETE -----------------------------");

	}

}
