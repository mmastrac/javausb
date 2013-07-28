package com.grack.libusb;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * A {@link PhantomReference} that also tracks a {@link LibUSBFinalizer}.
 */
class LibUSBFinalizerReference extends PhantomReference<Object> {
	private LibUSBFinalizer finalizer;

	public LibUSBFinalizerReference(Object referent, ReferenceQueue<? super Object> q, LibUSBFinalizer finalizer) {
		super(referent, q);
		this.finalizer = finalizer;
	}

	public LibUSBFinalizer getFinalizer() {
		return finalizer;
	}

	public String toString() {
		return "[Reference of " + finalizer.getClass() + "]";
	};
}