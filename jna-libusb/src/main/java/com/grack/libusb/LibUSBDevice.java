package com.grack.libusb;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.sun.jna.Pointer;

public class LibUSBDevice {
	private Pointer dev;
	LibUSB usb;
	libusb_device_descriptor descriptor;

	public LibUSBDevice(LibUSB usb, libusb_device_descriptor descriptor, Pointer dev) {
		this.usb = usb;
		this.descriptor = descriptor;
		this.dev = dev;
	}

	@Override
	protected void finalize() throws Throwable {
		System.err.println("Freeing device");
		usb.unrefDevice(dev);
	}

	public short vendor() {
		return descriptor.idVendor;
	}

	public short product() {
		return descriptor.idProduct;
	}

	public int numConfigurations() {
		return descriptor.bNumConfigurations;
	}
	
	public Iterable<LibUSBConfiguration> configurations() {
		return new Iterable<LibUSBConfiguration>() {
			@Override
			public Iterator<LibUSBConfiguration> iterator() {
				List<LibUSBConfiguration> configs = Lists.newArrayList();
				try {
					for (int i = 0; i < descriptor.bNumConfigurations; i++) {
						libusb_config_descriptor descriptor;
						descriptor = usb.getConfigDescriptor(dev, i);
						configs.add(new LibUSBConfiguration(descriptor));
					}
				} catch (LibUSBException e) {
					throw new LibUSBRuntimeException(e);
				}

				return configs.iterator();
			}
		};
	}

	public LibUSBOpenDevice open() throws LibUSBException {
		return new LibUSBOpenDevice(usb, this, usb.openDevice(dev));
	}

	@Override
	public String toString() {
		return String.format("Device %04x:%04x", vendor(), product());
	}
}
