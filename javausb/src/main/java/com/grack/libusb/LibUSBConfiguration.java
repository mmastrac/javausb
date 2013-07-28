package com.grack.libusb;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.grack.libusb.jna.libusb_config_descriptor;
import com.grack.libusb.jna.libusb_interface;

public class LibUSBConfiguration {
	private static final Logger logger = Logger.getLogger(LibUSBConfiguration.class.getName());

	private libusb_config_descriptor descriptor;
	private LibUSBDevice device;
	private ImmutableList<LibUSBInterface> interfaces;

	LibUSBConfiguration(final LibUSBDevice device, libusb_config_descriptor descriptor) {
		this.device = device;
		this.descriptor = descriptor;

		if (numInterfaces() == 0) {
			// This might not be possible, but may as well be defensive here
			interfaces = ImmutableList.of();
		} else {
			List<libusb_interface> list = Arrays.asList(descriptor.interfaces.toArray(numInterfaces()));

			interfaces = ImmutableList.copyOf(Lists.transform(list, new Function<libusb_interface, LibUSBInterface>() {
				public LibUSBInterface apply(libusb_interface input) {
					return new LibUSBInterface(device, input);
				}
			}));
		}

		device.usb.trackFinalizer(this, new LibUSBConfigurationFinalizer(device.usb, descriptor));
	}

	private static class LibUSBConfigurationFinalizer implements LibUSBFinalizer {
		private libusb_config_descriptor descriptor;
		private LibUSB usb;

		public LibUSBConfigurationFinalizer(LibUSB usb, libusb_config_descriptor descriptor) {
			this.usb = usb;
			this.descriptor = descriptor;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: configuration descriptor");
			usb.cleanup(descriptor);
		}
	}

	public int maxPowerInMilliamps() {
		return (descriptor.bMaxPower & 0xff) * 2;
	}

	public int numInterfaces() {
		return descriptor.bNumInterfaces;
	}

	public Iterable<LibUSBInterface> interfaces() {
		return interfaces;
	}

	@Override
	public String toString() {
		return "Configuration #" + descriptor.bConfigurationValue + " for " + device;
	}
}
