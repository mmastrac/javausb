package com.grack.javausb.jna;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_interface_descriptor extends Structure {
	public static class ByReference extends libusb_interface_descriptor implements Structure.ByReference {
	}

	public libusb_interface_descriptor() {
	}

	public libusb_interface_descriptor(Pointer p) {
		super(p);
		read();
	}
	
	public libusb_interface_descriptor[] toArray(int size) {
		return (libusb_interface_descriptor[]) super.toArray(size);
	}

	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList.of("bLength", "bDescriptorType", "bInterfaceNumber", "bAlternateSetting", "bNumEndpoints", "bInterfaceClass",
				"bInterfaceSubClass", "bInterfaceProtocol", "iInterface", "endpoint", "extra", "extra_length");
	}
	
	public byte bLength;
	public byte bDescriptorType;
	public byte bInterfaceNumber;
	public byte bAlternateSetting;
	public byte bNumEndpoints;
	public byte bInterfaceClass;
	public byte bInterfaceSubClass;
	public byte bInterfaceProtocol;
	public byte iInterface;
	public libusb_endpoint_descriptor.ByReference endpoint;
	public Pointer extra; // char*, length = extra_length
	public int extra_length;
}
