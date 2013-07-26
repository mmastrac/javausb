package com.grack.libusb;

import java.util.Iterator;

import com.sun.jna.Pointer;

public class LibUSBOpenDevice implements AutoCloseable {
	private LibUSBDevice device;
	private Pointer handle;
	private LibUSB usb;

	public LibUSBOpenDevice(LibUSB usb, LibUSBDevice device, Pointer handle) {
		this.usb = usb;
		this.device = device;
		this.handle = handle;
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
		if (handle == null)
			return;
		usb.closeDevice(handle);
		handle = null;
		usb = null;
		device = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
}
