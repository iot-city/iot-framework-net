package org.iotcity.iot.framework.net.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
	void add(NetInbound<?, ?> inbound, int priority) {
		synchronized (lock) {
			list.add(new NetInboundObject(inbound, priority));
			if (!modified) modified = true;
		}
	}

	/**
	 * Remove an inbound object from context.
	 * @param inbound Network inbound message processing object (not null).
	 */
	void remove(NetInbound<?, ?> inbound) {
		if (inbound == null) return;
		synchronized (lock) {
			Iterator<NetInboundObject> iterator = list.iterator();
			while (iterator.hasNext()) {
				NetInboundObject obj = iterator.next();
				if (inbound == obj.inbound) {
					iterator.remove();
					if (!modified) modified = true;
				}
			}
		}
	}

	/**
	 * Get inbound objects from context (returns null if there is no inbound in this context).
	 * @return Inbound objects in this context.
	 */
	NetInboundObject[] getInbounds() {
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
