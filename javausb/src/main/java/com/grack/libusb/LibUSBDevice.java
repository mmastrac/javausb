package com.grack.libusb;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.grack.libusb.jna.libusb_config_descriptor;
import com.grack.libusb.jna.libusb_device_descriptor;
import com.sun.jna.Pointer;

public class LibUSBDevice {
	Pointer dev;
	LibUSB usb;
	libusb_device_descriptor descriptor;

	private static final Logger logger = Logger.getLogger(LibUSBDevice.class.getName());

	LibUSBDevice(LibUSB usb, libusb_device_descriptor descriptor, Pointer dev) {
		this.usb = usb;
		this.descriptor = descriptor;
		this.dev = dev;

		usb.trackFinalizer(this, new LibUSBDeviceFinalizer(usb, dev));
	}

	private static class LibUSBDeviceFinalizer implements LibUSBFinalizer {
		private LibUSB usb;
		private Pointer dev;

		public LibUSBDeviceFinalizer(LibUSB usb, Pointer dev) {
			this.usb = usb;
			this.dev = dev;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: device");
			usb.unrefDevice(dev);
		}
	}

	public int vendor() {
		return descriptor.idVendor & 0xffff;
	}

	public int product() {
		return descriptor.idProduct & 0xffff;
	}

	public int numConfigurations() {
		return descriptor.bNumConfigurations;
	}

	/**
	 * Lists device configurations by sending requests to the device for each
	 * individual descriptor. Each call to configurations returns a brand new
	 * set of {@link LibUSBConfiguration} objects.
	 */
	public Iterable<LibUSBConfiguration> configurations() {
		return new Iterable<LibUSBConfiguration>() {
			@Override
			public Iterator<LibUSBConfiguration> iterator() {
				List<LibUSBConfiguration> configs = Lists.newArrayList();
				try {
					for (int i = 0; i < descriptor.bNumConfigurations; i++) {
						libusb_config_descriptor descriptor;
						descriptor = usb.getConfigDescriptor(dev, i);
						configs.add(new LibUSBConfiguration(LibUSBDevice.this, descriptor));
					}
				} catch (LibUSBException e) {
					throw new LibUSBRuntimeException(e);
				}

				return configs.iterator();
			}
		};
	}

	/**
	 * Opens a device.
	 */
	public LibUSBOpenDevice open() throws LibUSBException {
		return new LibUSBOpenDevice(usb, this, usb.openDevice(dev));
	}

	@Override
	public String toString() {
		return String.format("Device %04x:%04x", vendor(), product());
	}
}
