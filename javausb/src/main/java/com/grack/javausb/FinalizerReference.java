package com.grack.javausb;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * A {@link PhantomReference} that also tracks a {@link Finalizer}.
 */
class FinalizerReference extends PhantomReference<Object> {
	private Finalizer finalizer;

	public FinalizerReference(Object referent, ReferenceQueue<? super Object> q, Finalizer finalizer) {
		super(referent, q);
		this.finalizer = finalizer;
	}

	public Finalizer getFinalizer() {
		return finalizer;
	}

	public String toString() {
		return "[Reference of " + finalizer.getClass() + "]";
	};
}