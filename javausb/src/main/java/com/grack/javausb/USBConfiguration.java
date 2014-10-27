package com.grack.javausb;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.grack.javausb.jna.libusb_config_descriptor;
import com.grack.javausb.jna.libusb_interface;

public class USBConfiguration {
	private static final Logger logger = Logger.getLogger(USBConfiguration.class.getName());

	private libusb_config_descriptor descriptor;
	private USBDevice device;
	private ImmutableList<USBInterface> interfaces;

	USBConfiguration(USB usb, final USBDevice device, libusb_config_descriptor descriptor) {
		this.device = device;
		this.descriptor = descriptor;

		if (numInterfaces() == 0) {
			// This might not be possible, but may as well be defensive here
			interfaces = ImmutableList.of();
		} else {
			List<libusb_interface> list = Arrays.asList(descriptor.interfaces.toArray(numInterfaces()));

			interfaces = ImmutableList.copyOf(Lists.transform(list, new Function<libusb_interface, USBInterface>() {
				public USBInterface apply(libusb_interface input) {
					return new USBInterface(device, input);
				}
			}));
		}

		usb.trackFinalizer(this, new LibUSBConfigurationFinalizer(descriptor));
	}

	private static class LibUSBConfigurationFinalizer implements Finalizer {
		private libusb_config_descriptor descriptor;

		public LibUSBConfigurationFinalizer(libusb_config_descriptor descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: configuration descriptor");
			USBNative.cleanup(descriptor);
		}
	}

	public int maxPowerInMilliamps() {
		return (descriptor.bMaxPower & 0xff) * 2;
	}

	public int numInterfaces() {
		return descriptor.bNumInterfaces;
	}

	public Iterable<USBInterface> interfaces() {
		return interfaces;
	}

	public int number() {
		return descriptor.bConfigurationValue;
	}

	@Override
	public String toString() {
		return "Configuration #" + number()  + " for " + device;
	}
}
