package org.iotcity.iot.framework.net.io;

/**
 * The callback locker for asynchronous response to synchronous response transformation.
 * @author ardon
 * @date 2021-06-27
 */
public final class NetResponseCallbackLocker<RES extends NetDataResponse> extends NetResponseCallback<RES> {

	/**
	 * Response lock object.
	 */
	private final Object lock = new Object();
	/**
	 * Locked or unlocked status.
	 */
	private boolean locked = false;
	/**
	 * Indicates whether callback processing was executed.
	 */
	private boolean callbacked = false;

	/**
	 * Wait for asynchronous response.
	 * @param io The network input and output object (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (required, the parameter value can not be less than or equal to 0).
	 */
	public final void waitForResponse(NetIO<?, ?> io, long timeout) {
		if (locked || callbacked || timeout <= 0) return;

		synchronized (lock) {
			// Check progress status.
			if (locked || callbacked) return;
			// Set to locked.
			locked = true;

			// Waiting for response.
			try {
				lock.wait(timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Reset locked
			locked = false;

			// Callback timeout status when there is no response data callback.
			if (!callbacked) {
				try {
					callbackResult(new NetResponseResult<RES>(io, NetMessageStatus.RESPONSE_TIMEOUT, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Callback response result processing.
	 * @param result The network response result data (not null).
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public final void callbackResult(NetResponseResult<RES> result) throws Exception {
		if (callbacked || result == null) return;
		synchronized (lock) {
			if (callbacked) return;
			callbacked = true;
			this.result = result;
			if (locked) lock.notifyAll();
		}
	}

	@Override
	public final void onCallback(NetResponseResult<RES> result) throws Exception {
		// There is no need to handle this logic.
	}

}
