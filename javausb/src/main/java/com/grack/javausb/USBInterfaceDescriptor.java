package com.grack.javausb;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.grack.javausb.jna.libusb_endpoint_descriptor;
import com.grack.javausb.jna.libusb_interface_descriptor;

public class USBInterfaceDescriptor {
	private USBDevice device;
	private ImmutableList<USBEndpoint> endpoints;
	libusb_interface_descriptor descriptor;

	public USBInterfaceDescriptor(final USBDevice device, libusb_interface_descriptor descriptor) {
		this.device = device;
		this.descriptor = descriptor;
		if (numEndpoints() == 0)
			// This may not be possible, but may as well be defensive here
			endpoints = ImmutableList.of();
		else {
			List<libusb_endpoint_descriptor> list = Arrays.asList(descriptor.endpoint.toArray(numEndpoints()));
			endpoints = ImmutableList.copyOf(Lists.transform(list, new Function<libusb_endpoint_descriptor, USBEndpoint>() {
				public USBEndpoint apply(libusb_endpoint_descriptor input) {
					return new USBEndpoint(input);
				}
			}));
		}
	}

	public int subClass() {
		return descriptor.bInterfaceSubClass;
	}

	public int interfaceClass() {
		return descriptor.bInterfaceClass;
	}

	public int protocol() {
		return descriptor.bInterfaceProtocol;
	}

	public int alternateSetting() {
		return descriptor.bAlternateSetting;
	}

	public int numEndpoints() {
		return descriptor.bNumEndpoints;
	}

	public Iterable<USBEndpoint> endpoints() {
		return endpoints;
	}

	public String description(USBOpenDevice device) throws USBException {
		// TODO: This is somewhat awkward, although opening the device just to
		// read this might be as well.
		if (descriptor.iInterface == 0)
			return null;

		return device.getStringDescriptionAscii(descriptor.iInterface);
	}

	@Override
	public String toString() {
		return "Interface #" + descriptor.bInterfaceNumber + " alternate setting #" + descriptor.bAlternateSetting + " for " + device;
	}
}
