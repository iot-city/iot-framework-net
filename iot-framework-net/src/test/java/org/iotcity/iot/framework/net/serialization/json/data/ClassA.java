package org.iotcity.iot.framework.net.serialization.json.data;

import java.io.Serializable;

/**
 * @author ardon
 * @date 2021-09-06
 */
public class ClassA implements Serializable {

	private static final long serialVersionUID = 1L;

	public String name;
	public String desc;

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
