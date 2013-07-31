package com.grack.javausb;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.grack.javausb.jna.libusb_interface;
import com.grack.javausb.jna.libusb_interface_descriptor;

public class USBInterface {
	private libusb_interface iface;
	private USBDevice device;
	private ImmutableList<USBInterfaceDescriptor> altSettings;
	private USBInterfaceDescriptor first;

	USBInterface(final USBDevice device, libusb_interface iface) {
		this.device = device;
		this.iface = iface;

		if (numAltSettings() == 0) {
			// This may not be possible, but may as well be defensive here
			altSettings = ImmutableList.of();
		} else {
			List<libusb_interface_descriptor> altsettings = Arrays.asList(iface.altsetting.toArray(numAltSettings()));

			altSettings = ImmutableList.copyOf(Lists.transform(altsettings,
					new Function<libusb_interface_descriptor, USBInterfaceDescriptor>() {
						public USBInterfaceDescriptor apply(libusb_interface_descriptor input) {
							return new USBInterfaceDescriptor(device, input);
						}
					}));
		}
		
		first = altSettings.size() == 0 ? null : altSettings.get(0);
	}

	public int numAltSettings() {
		return this.iface.num_altsetting;
	}

	public Iterable<USBInterfaceDescriptor> altSettings() {
		return altSettings;
	}

	public int number() {
		if (first == null)
			// Unexpected
			throw new IllegalStateException("No alternate settings");
	
		return first.descriptor.bInterfaceNumber;	
	}

	@Override
	public String toString() {
		// The interface number is actually available in the altsettings -
		// libusb oddity. If for some reason altSessings is empty, just display
		// (?) instead.
		return "Interface #" + (first == null ? "(?)" : first.descriptor.bInterfaceNumber) + " for " + device;
	}

}
