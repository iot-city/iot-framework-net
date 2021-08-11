package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.iotcity.iot.framework.net.io.NetInbound;

/**
 * The inbound object context.
 * @author ardon
 * @date 2021-06-23
 */
public final class NetInboundContext {

	/**
	 * Lock for modified status.
	 */
	private final Object lock = new Object();
	/**
	 * The inbound classes.
	 */
	private final Set<Class<?>> classes = new HashSet<>();
	/**
	 * The inbound object list.
	 */
	private final List<NetInboundObject> list = new ArrayList<>();
	/**
	 * The inbound object array.
	 */
	private NetInboundObject[] inbounds = null;
	/**
	 * The inbound list status for modifying.
	 */
	private boolean modified = false;

	// --------------------------- Array comparator ----------------------------

	/**
	 * Inbound objects comparator for priority (the priority with the highest value is called first).
	 */
	private static final Comparator<NetInboundObject> COMPARATOR = new Comparator<NetInboundObject>() {

		@Override
		public int compare(NetInboundObject o1, NetInboundObject o2) {
			if (o1.priority == o2.priority) return 0;
			return o1.priority < o2.priority ? 1 : -1;
		}

	};

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Add an inbound object to context.
	 * @param inbound Network inbound message processing object (not null).
	 * @param priority Inbound message processing priority (the priority with the highest value is called first, 0 by default).
	 */
	final void add(NetInbound<?, ?> inbound, int priority) {
		Class<?> clazz = inbound.getClass();
		synchronized (lock) {
			if (classes.contains(clazz)) return;
			classes.add(clazz);
			list.add(new NetInboundObject(inbound, priority));
			if (!modified) modified = true;
		}
	}

	/**
	 * Remove an inbound object from context.
	 * @param inbound Network inbound message processing object (not null).
	 */
	final void remove(NetInbound<?, ?> inbound) {
		synchronized (lock) {
			Iterator<NetInboundObject> iterator = list.iterator();
			while (iterator.hasNext()) {
				NetInboundObject obj = iterator.next();
				if (inbound == obj.inbound) {
					iterator.remove();
					if (!modified) modified = true;
					classes.remove(inbound.getClass());
					break;
				}
			}
		}
	}

	/**
	 * Get inbound objects from context (returns null if there is no inbound in this context).
	 * @return Inbound objects in this context.
	 */
	final NetInboundObject[] getInbounds() {
		if (modified) {
			synchronized (lock) {
				if (modified) {
					if (list.size() == 0) {
						inbounds = null;
					} else {
						inbounds = list.toArray(new NetInboundObject[list.size()]);
						Arrays.sort(inbounds, COMPARATOR);
					}
					modified = false;
				}
			}
		}
		return inbounds;
	}

}
