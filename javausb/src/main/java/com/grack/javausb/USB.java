package com.grack.javausb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.grack.javausb.jna.LibUSBXNative;
import com.grack.javausb.jna.libusb_config_descriptor;
import com.grack.javausb.jna.libusb_device_descriptor;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class USB {
	private static final LibUSBXNative USB = LibUSBXNative.INSTANCE;
	private Pointer ctx;
	private FinalizationThread finalizationThread;

	private static final Logger logger = Logger.getLogger(USB.class.getName());
	
	public USB() throws USBException {
		this.ctx = openLibrary();
		// USB.libusb_set_debug(ctx, 4);
		
		finalizationThread = new FinalizationThread();
		finalizationThread.start();
		finalizationThread.track(this, new LibUSBCleanupFinalizer(openLibrary()));
		
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
			USB.libusb_exit(ctx);
		}
	}

	private Pointer openLibrary() throws USBException {
		PointerByReference ctxPtr = new PointerByReference();
		nativeCall(USB.libusb_init(ctxPtr));
		return ctxPtr.getValue();
	}

	private static int nativeCall(int response) throws USBException {
		if (response < 0)
			throw new USBException(response);
		return response;
	}

	public Iterable<USBDevice> devices() {
		return new Iterable<USBDevice>() {
			public Iterator<USBDevice> iterator() {
				// libusb_device ***list
				final PointerByReference list = new PointerByReference();
				final int count = USB.libusb_get_device_list(ctx, list);

				Pointer[] pointers = list.getValue().getPointerArray(0, count);

				// Eagerly transform this list
				ArrayList<USBDevice> descriptors = Lists.newArrayList(Iterables.transform(Arrays.asList(pointers),
						new Function<Pointer, USBDevice>() {
							public USBDevice apply(Pointer dev) {
								libusb_device_descriptor[] desc = new libusb_device_descriptor[1];
								try {
									nativeCall(USB.libusb_get_device_descriptor(dev, desc));
								} catch (USBException e) {
									throw new USBRuntimeException(e);
								}
								return new USBDevice(USB.this, desc[0], dev);
							}
						}));

				// Don't unref the devices, but do free the list
				USB.libusb_free_device_list(list.getValue(), false);

				return descriptors.iterator();
			}
		};
	}

	String getStringDescriptionAscii(Pointer dev, byte index) throws USBException {
		byte[] s = new byte[4096];
		nativeCall(USB.libusb_get_string_descriptor_ascii(dev, index, s, s.length));
		return Native.toString(s, "ASCII");
	}

	Pointer openDevice(Pointer dev) throws USBException {
		PointerByReference handle = new PointerByReference();
		nativeCall(USB.libusb_open(dev, handle));
		return handle.getValue();
	}

	void closeDevice(Pointer handle) {
		USB.libusb_close(handle);
	}

	void unrefDevice(Pointer dev) {
		USB.libusb_unref_device(dev);
	}

	libusb_config_descriptor getConfigDescriptor(Pointer dev, int index) throws USBException {
		PointerByReference config = new PointerByReference();
		nativeCall(USB.libusb_get_config_descriptor(dev, index, config));
		libusb_config_descriptor descriptor = new libusb_config_descriptor(config.getValue());
		return descriptor;
	}

	public FinalizerReference trackFinalizer(Object referent, Finalizer finalizer) {
		return finalizationThread.track(referent, finalizer);
	}

	public void forceFinalization(FinalizerReference finalizer) {
		finalizationThread.force(finalizer);
	}

	public void cleanup(libusb_config_descriptor descriptor) {
		USB.libusb_free_config_descriptor(descriptor.getPointer());
	}

	
//	<T> ImmutableList<T> structArrayToImmutableList(Pointer ptr) {
//		Structure.autoRead(ss)
//		return ImmutableList.copyOf(elements);
//	}
}
