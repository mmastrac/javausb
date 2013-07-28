package com.grack.libusb;

import java.io.Closeable;
import java.util.logging.Logger;

import com.sun.jna.Pointer;

public class LibUSBOpenDevice implements AutoCloseable, Closeable {
	private LibUSBDevice device;
	Pointer handle;
	LibUSB usb;
	private LibUSBFinalizerReference finalizer;

	private static final Logger logger = Logger.getLogger(LibUSBDevice.class.getName());

	public LibUSBOpenDevice(LibUSB usb, LibUSBDevice device, Pointer handle) {
		this.usb = usb;
		this.device = device;
		this.handle = handle;
		
		finalizer = usb.trackFinalizer(this, new LibUSBOpenDeviceFinalizer(usb, handle));
	}

	private static class LibUSBOpenDeviceFinalizer implements LibUSBFinalizer {
		private LibUSB usb;
		private Pointer handle;

		public LibUSBOpenDeviceFinalizer(LibUSB usb, Pointer handle) {
			this.handle = handle;
			this.usb = usb;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: open device");
			usb.closeDevice(handle);
		}
	}

	public String manufacturer() throws LibUSBException {
		if (device.descriptor.iManufacturer == 0)
			return null;

		return usb.getStringDescriptionAscii(handle, device.descriptor.iManufacturer);
	}

	public String product() throws LibUSBException {
		if (device.descriptor.iProduct == 0)
			return null;

		return usb.getStringDescriptionAscii(handle, device.descriptor.iProduct);
	}

	public String serialNumber() throws LibUSBException {
		if (device.descriptor.iSerialNumber == 0)
			return null;

		return usb.getStringDescriptionAscii(handle, device.descriptor.iSerialNumber);
	}

	@Override
	public synchronized void close() {
		if (finalizer != null) {
			usb.forceFinalization(finalizer);
			finalizer = null;
			handle = null;
			usb = null;
		}
	}
}
