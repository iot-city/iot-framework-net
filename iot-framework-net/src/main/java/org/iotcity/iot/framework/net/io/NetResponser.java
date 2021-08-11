package org.iotcity.iot.framework.net.io;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.net.FrameworkNet;
import org.iotcity.iot.framework.net.event.NetEventFactory;
import org.iotcity.iot.framework.net.event.NetMessageErrorEvent;

/**
 * The responser to process asynchronous response callback message.
 * @author ardon
 * @date 2021-06-25
 */
public final class NetResponser {

	// ------------------------------------- Private fields ---------------------------------

	/**
	 * The lock for responser data map.
	 */
	private final Object lock = new Object();
	/**
	 * The callback data map (the key is the message queue key, the value is the responser data context).
	 */
	private final Map<String, NetResponserContext> map = new HashMap<>();
	/**
	 * Indicates whether the responser context has changed.
	 */
	private boolean contextChanged = false;
	/**
	 * The responser context array.
	 */
	private NetResponserContext[] contexts = new NetResponserContext[0];

	// ------------------------------------- Callback methods ---------------------------------

	/**
	 * Add asynchronous response callback data.
	 * @param <REQ> The request data type.
	 * @param <RES> The response data type.
	 * @param io The network input and output object (required, can not be null).
	 * @param messageQueue The message queue key of the paired message request and response (required, can not be null or empty).
	 * @param request The request data object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param callback The asynchronous response callback processing object (required, can not be null).
	 * @param timeout The timeout value in milliseconds that waiting for a response data callback (required, the value is greater than 0).
	 */
	public final <REQ extends NetDataRequest, RES extends NetDataResponse> void addCallback(NetIO<?, ?> io, String messageQueue, REQ request, Class<RES> responseClass, NetResponseCallback<RES> callback, long timeout) {
		NetResponserObject object = new NetResponserObject(io, request, responseClass, callback, timeout);
		synchronized (lock) {
			NetResponserContext context = map.get(messageQueue);
			if (context == null) {
				contextChanged = true;
				context = new NetResponserContext(messageQueue, object);
				map.put(messageQueue, context);
			} else {
				context.add(object);
			}
		}
	}

	/**
	 * Try to execute the response data callback processing.
	 * @param direction The network message data transmission direction (required, not null).
	 * @param io The network input and output object (required, can not be null).
	 * @param messageQueue The message queue key of the paired message request and response (required, can not be null or empty).
	 * @param responseClass The response data class (required, can not be null).
	 * @param status The network message status (required, can not be null).
	 * @param response The network response data object (optional, set it to null when the response is not required).
	 * @return The number of successful callbacks.
	 */
	public final int tryCallback(NetMessageDirection direction, NetIO<?, ?> io, String messageQueue, Class<?> responseClass, NetMessageStatus status, NetDataResponse response) {
		// Get the responser data context.
		NetResponserContext context = map.get(messageQueue);
		if (context == null) return 0;
		// Get the responser data objects.
		NetResponserObject[] objects = context.remove(responseClass);
		if (objects == null) return 0;
		int successes = 0;
		// Traverse the objects to execute callback.
		for (NetResponserObject object : objects) {
			// Invoke callback.
			if (invokeCllaback(object, direction, io, responseClass, status, response)) successes++;
		}
		// Return the successes.
		return successes;
	}

	/**
	 * Check the timeout callback objects and execute the timeout callback processing.
	 * @param currentTime Current system time.
	 */
	public final void checkTimeout(long currentTime) {
		// Get responser data contexts.
		NetResponserContext[] contexts = getContexts(false);
		// Get the timeout status.
		NetMessageStatus status = NetMessageStatus.RESPONSE_TIMEOUT;
		// Traverse the contexts to get timeout objects.
		for (NetResponserContext context : contexts) {
			// Get timeout responser objects.
			NetResponserObject[] objects = context.removeTimeout(currentTime);
			if (objects == null) continue;
			// Traverse the objects to execute callback.
			for (NetResponserObject object : objects) {
				// Invoke callback.
				invokeCllaback(object, NetMessageDirection.TO_REMOTE_REQUEST, object.io, object.responseClass, status, null);
			}
		}
	}

	/**
	 * Execute all responser data callback before the channel closing.
	 */
	public final void callbackOnClosing() {
		// Get responser data contexts.
		NetResponserContext[] contexts = getContexts(true);
		// Get the closed status.
		NetMessageStatus status = NetMessageStatus.CHANNEL_CLOSING;
		// Traverse the contexts to get timeout objects.
		for (NetResponserContext context : contexts) {
			// Get timeout responser objects.
			NetResponserObject[] objects = context.removeAll();
			if (objects == null) continue;
			// Traverse the objects to execute callback.
			for (NetResponserObject object : objects) {
				// Invoke callback.
				invokeCllaback(object, NetMessageDirection.TO_REMOTE_REQUEST, object.io, object.responseClass, status, null);
			}
		}
	}

	// ------------------------------------- Private methods ---------------------------------

	/**
	 * Gets the responser context array.
	 * @param reset Whether to reset all responser contexts after returning data.
	 * @return The responser contexts.
	 */
	private final NetResponserContext[] getContexts(boolean reset) {
		// Get the contexts as the default returning data.
		NetResponserContext[] returns = contexts;
		if (contextChanged) {
			synchronized (lock) {
				// Rebuild the array.
				if (contextChanged) {
					contexts = map.values().toArray(new NetResponserContext[map.size()]);
					returns = contexts;
					contextChanged = false;
				}
				// Reset contexts.
				if (reset) {
					map.clear();
					contexts = new NetResponserContext[0];
				}
			}
		} else if (reset) {
			// Reset contexts only.
			synchronized (lock) {
				map.clear();
				contexts = new NetResponserContext[0];
			}
		}
		return returns;
	}

	/**
	 * Invoke the response callback.
	 * @param object The network asynchronous responser object (required, not null).
	 * @param direction The network message data transmission direction (required, not null).
	 * @param io The network input and output object (required, can not be null).
	 * @param responseClass The response data class (required, can not be null).
	 * @param status The network message status (required, can not be null).
	 * @param response The network response data object (optional, set it to null when the response is not required).
	 * @return Whether the callback execution is successful.
	 */
	private final boolean invokeCllaback(NetResponserObject object, NetMessageDirection direction, NetIO<?, ?> io, Class<?> responseClass, NetMessageStatus status, NetDataResponse response) {
		// Get the callback object.
		NetResponseCallback<?> callback = object.callback;
		try {
			// Execute callback.
			callback.callbackResponse(io, status, response);
			return true;
		} catch (Exception e) {
			// Log error message.
			FrameworkNet.getLogger().error(FrameworkNet.getLocale().text("net.message.logic.res.err", responseClass.getName(), e.getMessage()), e);
			// Get event factory and publisher.
			NetEventFactory factory = io.getService().getEventFactory();
			BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
			// Create an error event.
			NetMessageErrorEvent event = factory.createMessageErrorEvent(this, direction, io, object.request, response, new Exception[] {
				e
			}, NetMessageStatus.CALLBACK_EXCEPTION);
			// Publish the error event.
			publisher.publish(event);
			return false;
		}
	}

	// ------------------------------------- Inner classes ---------------------------------

	/**
	 * Network responser object context.
	 * @author ardon
	 * @date 2021-06-28
	 */
	final class NetResponserContext {

		/**
		 * The message queue key of the paired message request and response (not null or empty).
		 */
		private final String messageQueue;
		/**
		 * The array for responser data objects.
		 */
		private NetResponserObject[] array;

		/**
		 * Constructor for responser object context.
		 * @param messageQueue The message queue key of the paired message request and response (not null or empty).
		 * @param object The network asynchronous responser object (not null).
		 */
		NetResponserContext(String messageQueue, NetResponserObject object) {
			this.messageQueue = messageQueue;
			array = new NetResponserObject[] {
				object
			};
		}

		/**
		 * Add responser data object to context.
		 * @param object The network asynchronous responser object (not null).
		 */
		final synchronized void add(NetResponserObject object) {
			int len = array.length;
			if (len == 0) {
				array = new NetResponserObject[] {
					object
				};
			} else {
				NetResponserObject[] newArray = new NetResponserObject[len + 1];
				System.arraycopy(array, 0, newArray, 0, len);
				newArray[len] = object;
				array = newArray;
			}
		}

		/**
		 * Remove and get the response objects by specified response class (returns null if not found).
		 * @param responseClass The response data class (not null).
		 * @return The network asynchronous responser objects.
		 */
		final NetResponserObject[] remove(Class<?> responseClass) {
			// Remove objects by response class.
			NetResponserObject[] objects = removeByFilter(new NetResponserFilter() {

				@Override
				public boolean test(NetResponserObject object) {
					return object.responseClass.isAssignableFrom(responseClass);
				}

			});
			// NOTE:
			// It is forbidden to use 'synchronized' to lock the current method to obtain the length of the array to prevent deadlock.
			// Since the length only increases when the callback be added to this context, this processing method is thread safe.
			if (array.length == 0) {
				// Remove the context that haven't responser object.
				synchronized (lock) {
					if (array.length == 0) {
						contextChanged = true;
						map.remove(messageQueue);
					}
				}
			}
			// Return removing objects.
			return objects;
		}

		/**
		 * Remove and return the callback objects that has timed out (returns null if not found).
		 * @param time Current system time.
		 * @return The network asynchronous responser objects.
		 */
		final NetResponserObject[] removeTimeout(long time) {
			// Remove objects by timeout.
			NetResponserObject[] objects = removeByFilter(new NetResponserFilter() {

				@Override
				public boolean test(NetResponserObject object) {
					return time > object.createTime + object.timeout;
				}

			});
			// NOTE:
			// It is forbidden to use 'synchronized' to lock the current method to obtain the length of the array to prevent deadlock.
			// Since the length only increases when the callback be added to this context, this processing method is thread safe.
			if (array.length == 0) {
				// Remove the context that haven't responser object.
				synchronized (lock) {
					if (array.length == 0) {
						contextChanged = true;
						map.remove(messageQueue);
					}
				}
			}
			// Return removing objects.
			return objects;
		}

		/**
		 * Remove all responser data objects.
		 * @return The network asynchronous responser objects.
		 */
		final synchronized NetResponserObject[] removeAll() {
			NetResponserObject[] ret = array;
			array = new NetResponserObject[0];
			return ret;
		}

		/**
		 * Remove and get the response objects by specified filter (returns null if not found).
		 * @param filter The response data filter (not null).
		 * @return The network asynchronous responser objects.
		 */
		private final synchronized NetResponserObject[] removeByFilter(NetResponserFilter filter) {
			int len = array.length;
			if (len == 1) {
				// There is only one responser data.
				if (filter.test(array[0])) {
					NetResponserObject[] ret = array;
					array = new NetResponserObject[0];
					return ret;
				} else {
					return null;
				}
			} else if (len > 1) {
				// There is more than one responser data.
				NetResponserObject[] returns = new NetResponserObject[len];
				NetResponserObject[] remains = new NetResponserObject[len];
				int returnPos = 0;
				int remainPos = 0;
				// Filter responser object.
				for (int i = 0; i < len; i++) {
					NetResponserObject object = array[i];
					if (filter.test(object)) {
						returns[returnPos++] = object;
					} else {
						remains[remainPos++] = object;
					}
				}
				// Rebuild array objects.
				if (returnPos > 0) {
					if (remainPos > 0) {
						array = new NetResponserObject[remainPos];
						System.arraycopy(remains, 0, array, 0, remainPos);
					} else {
						array = new NetResponserObject[0];
					}
				}
				// Return found objects.
				if (returnPos < len) {
					if (returnPos == 0) {
						return null;
					} else {
						NetResponserObject[] ret = new NetResponserObject[returnPos];
						System.arraycopy(returns, 0, ret, 0, returnPos);
						return ret;
					}
				} else {
					return returns;
				}
			} else {
				// No responser data.
				return null;
			}
		}

	}

	/**
	 * The network responser filter.
	 * @author ardon
	 * @date 2021-06-28
	 */
	interface NetResponserFilter {

		/**
		 * Test the responser object.
		 * @param object The responser data object to test.
		 * @return Returns true if the test is successful, otherwise, returns false.
		 */
		boolean test(NetResponserObject object);

	}

	/**
	 * The network asynchronous responser object.
	 * @author ardon
	 * @date 2021-06-27
	 */
	final class NetResponserObject {

		/**
		 * The network input and output object (not null).
		 */
		final NetIO<?, ?> io;
		/**
		 * The request data object (not null).
		 */
		final NetDataRequest request;
		/**
		 * The response data class (not null).
		 */
		final Class<?> responseClass;
		/**
		 * The asynchronous response callback processing object (not null).
		 */
		final NetResponseCallback<?> callback;
		/**
		 * The timeout value in milliseconds that waiting for a response data callback.
		 */
		final long timeout;
		/**
		 * The time in milliseconds for responser object creation.
		 */
		final long createTime;

		/**
		 * Constructor for network asynchronous responser object.
		 * @param io The network input and output object (required, can not be null).
		 * @param request The request data object (required, can not be null).
		 * @param responseClass The response data class (required, can not be null).
		 * @param callback The asynchronous response callback processing object (required, can not be null).
		 * @param timeout The timeout value in milliseconds that waiting for a response data callback (required, the value is greater than 0).
		 */
		NetResponserObject(NetIO<?, ?> io, NetDataRequest request, Class<?> responseClass, NetResponseCallback<?> callback, long timeout) {
			this.io = io;
			this.request = request;
			this.responseClass = responseClass;
			this.callback = callback;
			this.timeout = timeout;
			this.createTime = System.currentTimeMillis();
		}

	}

}
