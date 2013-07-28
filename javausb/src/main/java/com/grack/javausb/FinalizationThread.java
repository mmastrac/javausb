package com.grack.javausb;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.common.collect.Sets;

/**
 * Manages cleanup for native LibUSB objects.
 */
class FinalizationThread {
	private static final Logger logger = Logger.getLogger(FinalizationThread.class.getName());

	/**
	 * The {@link ReferenceQueue} on which the {@link PhantomReference}s are
	 * enqueued when they are dead.
	 */
	private ReferenceQueue<Object> references = new ReferenceQueue<>();

	/**
	 * Ensure that the {@link PhantomReference}s don't get GC'd before they can
	 * be enqueued.
	 */
	private HashSet<FinalizerReference> finalizers = Sets.newHashSet();

	/**
	 * Our {@link ReferenceQueue} tracking thread.
	 */
	private Thread thread;
	
	public FinalizationThread() {
	}

	public void start() {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				go();
			}
		});
		thread.setDaemon(true);
		thread.setName("libusb native finalization thread");
		thread.start();
	}

	public FinalizerReference track(Object referent, Finalizer finalizer) {
		FinalizerReference ref = new FinalizerReference(referent, references, finalizer);
		synchronized (finalizers) {
			finalizers.add(ref);
		}
		return ref;
	}

	public void force(FinalizerReference finalizer) {
		synchronized (finalizers) {
			if (!finalizers.remove(finalizer)) {
				logger.severe("Double-finalization attempted, bug");
				return;
			}
		}

		finalizer.getFinalizer().cleanup();
		finalizer.clear();
	}

	private void go() {
		while (true) {
			try {
				FinalizerReference ref = (FinalizerReference) references.remove();
				ref.getFinalizer().cleanup();
				ref.clear();
				synchronized (finalizers) {
					if (!finalizers.remove(ref)) {
						logger.severe("Double-finalization attempted, bug");
						return;
					}
					
					if (finalizers.size() == 0) {
						logger.info("No more references found, shutting down");
					}
					
					logger.info(finalizers.size() + " remaining reference(s): " + finalizers);
				}
			} catch (InterruptedException e) {
				// Just abort here - someone obviously wants us dead and must
				// not care about leaking (hopefilly it's just the stop() method)
				logger.info("Aborting finalizer tracking thread");
				return;
			}
		}
	}
}
