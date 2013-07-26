package com.grack.libusb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class LibUSB {
	private static final LibUSBNative USB = LibUSBNative.INSTANCE;
	private Pointer ctx;

	public LibUSB() throws LibUSBException {
		PointerByReference ctxPtr = new PointerByReference();
		nativeCall(USB.libusb_init(ctxPtr));
		this.ctx = ctxPtr.getValue();
		// USB.libusb_set_debug(ctx, 4);
	}

	private static int nativeCall(int response) throws LibUSBException {
		if (response < 0)
			throw new LibUSBException(response);
		return response;
	}

	/**
	 * libusb shutdown.
	 */
	protected void finalize() {
		System.err.println("Freeing libusb");
		USB.libusb_exit(ctx);
	}

	public Iterable<LibUSBDevice> devices() {
		return new Iterable<LibUSBDevice>() {
			public Iterator<LibUSBDevice> iterator() {
				// libusb_device ***list
				final PointerByReference list = new PointerByReference();
				final int count = USB.libusb_get_device_list(ctx, list);

				Pointer[] pointers = list.getValue().getPointerArray(0, count);

				// Eagerly transform this list
				ArrayList<LibUSBDevice> descriptors = Lists.newArrayList(Iterables.transform(Arrays.asList(pointers),
						new Function<Pointer, LibUSBDevice>() {
							public LibUSBDevice apply(Pointer dev) {
								libusb_device_descriptor[] desc = new libusb_device_descriptor[1];
								try {
									nativeCall(USB.libusb_get_device_descriptor(dev, desc));
								} catch (LibUSBException e) {
									throw new LibUSBRuntimeException(e);
								}
								return new LibUSBDevice(LibUSB.this, desc[0], dev);
							}
						}));

				// Don't unref the devices, but do free the list
				USB.libusb_free_device_list(list.getValue(), false);

				return descriptors.iterator();
			}
		};
	}

	public String getStringDescriptionAscii(Pointer dev, byte index) throws LibUSBException {
		byte[] s = new byte[4096];
		nativeCall(USB.libusb_get_string_descriptor_ascii(dev, index, s, s.length));
		return Native.toString(s, "ASCII");
	}

	public Pointer openDevice(Pointer dev) throws LibUSBException {
		PointerByReference handle = new PointerByReference();
		nativeCall(USB.libusb_open(dev, handle));
		return handle.getValue();
	}

	public void closeDevice(Pointer handle) {
		USB.libusb_close(handle);
	}

	public void unrefDevice(Pointer dev) {
		USB.libusb_unref_device(dev);
	}

	public libusb_config_descriptor getConfigDescriptor(Pointer dev, int index) throws LibUSBException {
		PointerByReference config = new PointerByReference();
		nativeCall(USB.libusb_get_config_descriptor(dev, index, config));
		libusb_config_descriptor descriptor = new libusb_config_descriptor(config.getValue());
		USB.libusb_free_config_descriptor(config.getValue());
		return descriptor;
	}
}
