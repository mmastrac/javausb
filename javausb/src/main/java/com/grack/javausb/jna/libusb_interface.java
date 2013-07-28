package com.grack.javausb.jna;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_interface extends Structure {
	public static class ByReference extends libusb_interface implements Structure.ByReference {
	}

	public libusb_interface() {
	}

	public libusb_interface(Pointer p) {
		super(p);
		read();
	}

	public libusb_interface[] toArray(int size) {
		return (libusb_interface[]) super.toArray(new libusb_interface[size]);
	}

	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList.of("altsetting", "num_altsetting");
	}

	public libusb_interface_descriptor.ByReference altsetting;
	public int num_altsetting;
}
