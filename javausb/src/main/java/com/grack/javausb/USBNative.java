package com.grack.javausb;

import java.nio.ByteBuffer;

import com.grack.javausb.jna.LibUSBXNative;
import com.grack.javausb.jna.libusb_config_descriptor;
import com.grack.javausb.jna.libusb_device_descriptor;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Rather than everyone calling LibUSB from all over, we go through a number of
 * nice methods in one central place.
 */
class USBNative {
	private static final LibUSBXNative USB = LibUSBXNative.INSTANCE;

	static void cleanup(libusb_config_descriptor descriptor) {
		USB.libusb_free_config_descriptor(descriptor.getPointer());
	}

	static Pointer openLibrary() throws USBException {
		PointerByReference ctxPtr = new PointerByReference();
		nativeCall(USB.libusb_init(ctxPtr));
		return ctxPtr.getValue();
	}

	private static int nativeCall(int response) throws USBException {
		if (response < 0)
			throw new USBException(response);
		return response;
	}

	static String getStringDescriptionAscii(Pointer dev, byte index) throws USBException {
		byte[] s = new byte[4096];
		nativeCall(USB.libusb_get_string_descriptor_ascii(dev, index, s, s.length));
		return Native.toString(s, "ASCII");
	}

	static Pointer openDevice(Pointer dev) throws USBException {
		PointerByReference handle = new PointerByReference();
		nativeCall(USB.libusb_open(dev, handle));
		return handle.getValue();
	}

	static void closeDevice(Pointer handle) {
		USB.libusb_close(handle);
	}

	static void unrefDevice(Pointer dev) {
		USB.libusb_unref_device(dev);
	}

	static libusb_config_descriptor getConfigDescriptor(Pointer dev, int index) throws USBException {
		PointerByReference config = new PointerByReference();
		nativeCall(USB.libusb_get_config_descriptor(dev, index, config));
		libusb_config_descriptor descriptor = new libusb_config_descriptor(config.getValue());
		return descriptor;
	}

	static int bulkTransfer(Pointer handle, int endpoint, byte[] b, int off, int len) throws USBException {
		IntByReference transferred = new IntByReference();
		if (off == 0) {
			nativeCall(USB.libusb_bulk_transfer(handle, (byte) endpoint, b, len, transferred, 1000));
		} else {
			ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
			nativeCall(USB.libusb_bulk_transfer(handle, (byte) endpoint, buffer, len, transferred, 1000));
		}
		return transferred.getValue();
	}

	static void claimInterface(Pointer handle, int iface) throws USBException {
		nativeCall(USB.libusb_claim_interface(handle, iface));
	}

	static void setConfiguration(Pointer handle, int configuration) throws USBException {
		nativeCall(USB.libusb_set_configuration(handle, configuration));
	}

	static int sendControlTransfer(Pointer handle, int requestType, int request, int value, int index, byte[] buffer, int off, int len)
			throws USBException {
		// TODO: This should be a parameter?
		int timeout = 1000;
		return nativeCall(USB.libusb_control_transfer(handle, (byte) requestType, (byte) request, (short) value, (short) index,
				ByteBuffer.wrap(buffer, off, len), (short) len, timeout));
	}

	static void freeLibrary(Pointer ctx) {
		USB.libusb_exit(ctx);
	}

	static libusb_device_descriptor getDeviceDescriptor(Pointer dev) throws USBException {
		libusb_device_descriptor[] desc = new libusb_device_descriptor[1];
		nativeCall(USB.libusb_get_device_descriptor(dev, desc));
		return desc[0];
	}
	
	public static Pointer[] getDeviceHandles(Pointer ctx) {
		// libusb_device*** list
		final PointerByReference list = new PointerByReference();
		final int count = USB.libusb_get_device_list(ctx, list);
		Pointer[] pointers = list.getValue().getPointerArray(0, count);
		USB.libusb_free_device_list(list.getValue(), false);
		return pointers;
	}
}
