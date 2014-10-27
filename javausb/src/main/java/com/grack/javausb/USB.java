package com.grack.javausb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.grack.javausb.jna.libusb_device_descriptor;
import com.sun.jna.Pointer;

public class USB {
	private Pointer ctx;
	private FinalizationThread finalizationThread;

	private static final Logger logger = Logger.getLogger(USB.class.getName());

	public USB() throws USBException {
		finalizationThread = new FinalizationThread();
		finalizationThread.start();
		finalizationThread.track(this, new LibUSBCleanupFinalizer(USBNative.openLibrary()));

		logger.info("Initialized LibUSB JNA wrapper");
	}

	private static class LibUSBCleanupFinalizer implements Finalizer {
		private Pointer ctx;

		public LibUSBCleanupFinalizer(Pointer ctx) {
			this.ctx = ctx;
		}

		@Override
		public void cleanup() {
			// Free the library context
			logger.info("Freeing libusb");
			USBNative.freeLibrary(ctx);
		}
	}

	public Iterable<USBDevice> devices() {
		return new Iterable<USBDevice>() {
			public Iterator<USBDevice> iterator() {
				// libusb_device ***list
				Pointer[] pointers = USBNative.getDeviceHandles(ctx);

				// Eagerly transform this list
				ArrayList<USBDevice> descriptors = Lists.newArrayList(Iterables.transform(Arrays.asList(pointers),
						new Function<Pointer, USBDevice>() {
							public USBDevice apply(Pointer dev) {
								libusb_device_descriptor desc;
								try {
									desc = USBNative.getDeviceDescriptor(dev);
								} catch (USBException e) {
									throw new USBRuntimeException(e);
								}
								return new USBDevice(USB.this, desc, dev);
							}
						}));

				return descriptors.iterator();
			}
		};
	}

	FinalizerReference trackFinalizer(Object referent, Finalizer finalizer) {
		return finalizationThread.track(referent, finalizer);
	}

	void forceFinalization(FinalizerReference finalizer) {
		finalizationThread.force(finalizer);
	}
}
