package org.iotcity.iot.framework.net.kafka.data;

import java.io.Serializable;

/**
 * @author ardon
 * @date 2021-09-06
 */
public class ClassB implements Serializable {

	private static final long serialVersionUID = 1L;

	public int index;
	public String name;
	public String desc;

	public ClassB() {
	}

	public ClassB(int index, String name, String desc) {
		this.index = index;
		this.name = name;
		this.desc = desc;
	}

	public String toString() {
		return "ClassB-" + index + ":" + name;
	}

}
