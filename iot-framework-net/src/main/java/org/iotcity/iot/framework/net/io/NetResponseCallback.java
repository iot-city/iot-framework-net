package org.iotcity.iot.framework.net.io;

/**
 * Asynchronous response callback processing object.
 * @param <RES> The response data type.
 * @author ardon
 * @date 2021-06-22
 */
public abstract class NetResponseCallback<RES extends NetDataResponse> {

	/**
	 * Response lock object.
	 */
	private final Object lock = new Object();
	/**
	 * Indicates whether callback processing was executed.
	 */
	private boolean callbacked = false;
	/**
	 * The response result object.
	 */
	protected NetResponseResult<RES> result;

	/**
	 * Callback response data processing.
	 * @param io The network input and output object (required, can not be null).
	 * @param status The network message status (required, can not be null).
	 * @param response The network response data object (optional, set it to null when the response is not required).
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	@SuppressWarnings("unchecked")
	public void callbackResponse(NetIO<?, ?> io, NetMessageStatus status, NetDataResponse response) throws Exception {
		callbackResult(new NetResponseResult<RES>(io, status, (RES) response));
	}

	/**
	 * Callback response result processing.
	 * @param result The network response result data (required, can not be null).
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public void callbackResult(NetResponseResult<RES> result) throws Exception {
		if (callbacked || result == null) return;
		synchronized (lock) {
			if (callbacked) return;
			callbacked = true;
			this.result = result;
		}
		onCallback(result);
	}

	/**
	 * Gets the response result object (returns null if the callback is not executed).
	 */
	public NetResponseResult<RES> getResult() {
		return result;
	}

	/**
	 * On response result callback processing.
	 * @param result The network response result data (not null).
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public abstract void onCallback(NetResponseResult<RES> result) throws Exception;

}
