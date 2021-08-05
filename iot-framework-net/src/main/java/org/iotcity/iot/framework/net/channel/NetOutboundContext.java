package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.iotcity.iot.framework.net.io.NetOutbound;

/**
 * The outbound object context.
 * @author ardon
 * @date 2021-06-23
 */
public final class NetOutboundContext {

	/**
	 * Lock for modified status.
	 */
	private final Object lock = new Object();
	/**
	 * The outbound classes.
	 */
	private final Set<Class<?>> classes = new HashSet<>();
	/**
	 * The outbound object list.
	 */
	private final List<NetOutboundObject> list = new ArrayList<>();
	/**
	 * The outbound object array.
	 */
	private NetOutboundObject[] outbounds = null;
	/**
	 * The outbound list status for modifying.
	 */
	private boolean modified = false;

	// --------------------------- Array comparator ----------------------------

	/**
	 * Outbound objects comparator for priority (the priority with the highest value is called first).
	 */
	private static final Comparator<NetOutboundObject> COMPARATOR = new Comparator<NetOutboundObject>() {

		@Override
		public int compare(NetOutboundObject o1, NetOutboundObject o2) {
			if (o1.priority == o2.priority) return 0;
			return o1.priority < o2.priority ? 1 : -1;
		}

	};

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Add an outbound object to context.
	 * @param outbound Network outbound message processing object (not null).
	 * @param priority Outbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	void add(NetOutbound<?, ?> outbound, int priority) {
		Class<?> clazz = outbound.getClass();
		synchronized (lock) {
			if (classes.contains(clazz)) return;
			classes.add(clazz);
			list.add(new NetOutboundObject(outbound, priority));
			if (!modified) modified = true;
		}
	}

	/**
	 * Remove an outbound object from context.
	 * @param outbound Network outbound message processing object (not null).
	 */
	void remove(NetOutbound<?, ?> outbound) {
		synchronized (lock) {
			Iterator<NetOutboundObject> iterator = list.iterator();
			while (iterator.hasNext()) {
				NetOutboundObject obj = iterator.next();
				if (outbound == obj.outbound) {
					iterator.remove();
					if (!modified) modified = true;
					classes.remove(outbound.getClass());
					break;
				}
			}
		}
	}

	/**
	 * Get outbound objects from context (returns null if there is no outbound in this context).
	 * @return Outbound objects in this context.
	 */
	NetOutboundObject[] getOutbounds() {
		if (modified) {
			synchronized (lock) {
				if (modified) {
					if (list.size() == 0) {
						outbounds = null;
					} else {
						outbounds = list.toArray(new NetOutboundObject[list.size()]);
						Arrays.sort(outbounds, COMPARATOR);
					}
					modified = false;
				}
			}
		}
		return outbounds;
	}

}
