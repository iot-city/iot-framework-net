package org.iotcity.iot.framework.net.io;

/**
 * The response result group data.
 * @author ardon
 * @date 2021-07-02
 */
public class NetResponseResultGroup<RES extends NetDataResponse> {

	/**
	 * The number of response data executed successfully.
	 */
	private final int successes;
	/**
	 * The response result data array (not null).
	 */
	private final NetResponseResult<RES>[] results;

	/**
	 * Constructor for The response result group data.
	 * @param successes The number of response data executed successfully.
	 * @param results The response result data array (required, can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "results" is null.
	 */
	public NetResponseResultGroup(int successes, NetResponseResult<RES>[] results) {
		if (results == null) throw new IllegalArgumentException("Parameter results can not be null!");
		this.successes = successes;
		this.results = results;
	}

	/**
	 * Gets the number of response data executed successfully.
	 */
	public int getSuccesses() {
		return successes;
	}

	/**
	 * Gets the response result data array (returns not null).
	 */
	public NetResponseResult<RES>[] getResults() {
		return results;
	}

}
