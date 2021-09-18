package org.iotcity.iot.framework.net.serialization.json.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * @author ardon
 * @date 2021-09-06
 */
public class ClassA implements Serializable {

	private static final long serialVersionUID = 1L;

	public String name;
	public String desc;
	public int count = new Random().nextInt();
	public double total = new Random().nextDouble();
	public String uuid = StringHelper.getUUID();
	public Date time = new Date();

	public ClassA() {
	}

	public ClassA(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String toString() {
		return "ClassA-" + name;
	}

}
