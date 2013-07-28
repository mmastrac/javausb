package com.grack.javausb.jna;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_endpoint_descriptor extends Structure {
	public static class ByReference extends libusb_endpoint_descriptor implements Structure.ByReference {
	}

	public libusb_endpoint_descriptor() {
	}

	public libusb_endpoint_descriptor(Pointer p) {
		super(p);
		read();
	}

	public libusb_endpoint_descriptor[] toArray(int size) {
		return (libusb_endpoint_descriptor[]) super.toArray(size);
	}

	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList.of("bLength", "bDescriptorType", "bEndpointAddress", "bmAttributes", "wMaxPacketSize", "bInterval",
				"bRefresh", "bSynchAddress", "extra", "extra_length");
	}

	public byte bLength;
	public byte bDescriptorType;
	public byte bEndpointAddress;
	public byte bmAttributes;
	public short wMaxPacketSize;
	public byte bInterval;
	public byte bRefresh;
	public byte bSynchAddress;
	public Pointer extra; // char*, length = extra_length
	public int extra_length;
}
