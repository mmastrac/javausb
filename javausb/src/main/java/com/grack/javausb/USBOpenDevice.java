package com.grack.javausb;

import java.io.Closeable;
import java.util.logging.Logger;

import com.sun.jna.Pointer;

public class USBOpenDevice implements AutoCloseable, Closeable {
	private USBDevice device;
	private Pointer handle;
	private USB usb;
	private FinalizerReference finalizer;

	private static final Logger logger = Logger.getLogger(USBDevice.class.getName());

	public USBOpenDevice(USB usb, USBDevice device, Pointer handle) {
		this.usb = usb;
		this.device = device;
		this.handle = handle;
		
		finalizer = usb.trackFinalizer(this, new LibUSBOpenDeviceFinalizer(usb, handle));
	}

	private static class LibUSBOpenDeviceFinalizer implements Finalizer {
		private USB usb;
		private Pointer handle;

		public LibUSBOpenDeviceFinalizer(USB usb, Pointer handle) {
			this.handle = handle;
			this.usb = usb;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: open device");
			usb.closeDevice(handle);
		}
	}

	public String getStringDescriptionAscii(int index) throws USBException {
		return usb.getStringDescriptionAscii(handle, (byte)index);
	}
	
	public String manufacturer() throws USBException {
		if (device.descriptor.iManufacturer == 0)
			return null;

		return usb.getStringDescriptionAscii(handle, device.descriptor.iManufacturer);
	}

	public String product() throws USBException {
		if (device.descriptor.iProduct == 0)
			return null;

		return usb.getStringDescriptionAscii(handle, device.descriptor.iProduct);
	}

	public String serialNumber() throws USBException {
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
