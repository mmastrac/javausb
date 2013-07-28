package com.grack.javausb.jna;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_config_descriptor extends Structure {
	public static class ByReference extends libusb_config_descriptor implements Structure.ByReference {
	}

	public libusb_config_descriptor() {
	}

	public libusb_config_descriptor(Pointer p) {
		super(p);
		read();
	}

	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList.of("bLength", "bDescriptorType", "wTotalLength", "bNumInterfaces", "bConfigurationValue", "iConfiguration",
				"bmAttributes", "bMaxPower", "interfaces", "extra", "extra_length");
	}

	public byte bLength;
	public byte bDescriptorType;
	public short wTotalLength;
	public byte bNumInterfaces;
	public byte bConfigurationValue;
	public byte iConfiguration;
	public byte bmAttributes;
	public byte bMaxPower;
	public libusb_interface.ByReference interfaces; // libusb_interface, length = bNumInterfaces
	public Pointer extra; // char*, length = extra_length
	public int extra_length;
}
