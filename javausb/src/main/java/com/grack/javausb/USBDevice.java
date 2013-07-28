package com.grack.javausb;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.grack.javausb.jna.libusb_config_descriptor;
import com.grack.javausb.jna.libusb_device_descriptor;
import com.sun.jna.Pointer;

public class USBDevice {
	private Pointer dev;
	private USB usb;
	libusb_device_descriptor descriptor;

	private static final Logger logger = Logger.getLogger(USBDevice.class.getName());

	USBDevice(USB usb, libusb_device_descriptor descriptor, Pointer dev) {
		this.usb = usb;
		this.descriptor = descriptor;
		this.dev = dev;

		usb.trackFinalizer(this, new LibUSBDeviceFinalizer(usb, dev));
	}

	private static class LibUSBDeviceFinalizer implements Finalizer {
		private USB usb;
		private Pointer dev;

		public LibUSBDeviceFinalizer(USB usb, Pointer dev) {
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
	 * set of {@link USBConfiguration} objects.
	 */
	public Iterable<USBConfiguration> configurations() {
		return new Iterable<USBConfiguration>() {
			@Override
			public Iterator<USBConfiguration> iterator() {
				List<USBConfiguration> configs = Lists.newArrayList();
				try {
					for (int i = 0; i < descriptor.bNumConfigurations; i++) {
						libusb_config_descriptor descriptor;
						descriptor = usb.getConfigDescriptor(dev, i);
						configs.add(new USBConfiguration(usb, USBDevice.this, descriptor));
					}
				} catch (USBException e) {
					throw new USBRuntimeException(e);
				}

				return configs.iterator();
			}
		};
	}

	/**
	 * Opens a device.
	 */
	public USBOpenDevice open() throws USBException {
		return new USBOpenDevice(usb, this, usb.openDevice(dev));
	}

	@Override
	public String toString() {
		return String.format("Device %04x:%04x", vendor(), product());
	}
}
