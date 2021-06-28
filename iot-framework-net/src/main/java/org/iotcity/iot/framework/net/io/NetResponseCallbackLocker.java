package org.iotcity.iot.framework.net.io;

/**
 * The callback locker for asynchronous response to synchronous response transformation.
 * @author ardon
 * @date 2021-06-27
 */
public class NetResponseCallbackLocker<RES extends NetDataResponse> extends NetResponseCallback<RES> {

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
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 */
	public void waitForResponse(long timeout) {
		if (locked || callbacked) return;
		if (timeout <= 0) timeout = 60000;

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
					callbackResult(new NetResponseResult<RES>(NetMessageStatus.TIMEOUT, null));
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
	public void callbackResult(NetResponseResult<RES> result) throws Exception {
		if (callbacked) return;
		synchronized (lock) {
			if (callbacked) return;
			callbacked = true;
			this.result = result;
			if (locked) lock.notifyAll();
		}
	}

	@Override
	public void onCallback(NetResponseResult<RES> result) throws Exception {
	}

}
