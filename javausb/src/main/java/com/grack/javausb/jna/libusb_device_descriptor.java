package com.grack.javausb.jna;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_device_descriptor extends Structure {
	public static class ByReference extends libusb_device_descriptor implements Structure.ByReference {
	}

	public libusb_device_descriptor() {
	}

	public libusb_device_descriptor(Pointer p) {
		super(p);
		read();
	}

	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList
				.of("bLength", "bDescriptorType", "bcdUSB", "bDeviceClass", "bDeviceSubClass", "bDeviceProtocol", "bMaxPacketSize0",
						"idVendor", "idProduct", "bcdDevice", "iManufacturer", "iProduct", "iSerialNumber", "bNumConfigurations");
	}

	public byte bLength;
	public byte bDescriptorType;
	public short bcdUSB;
	public byte bDeviceClass;
	public byte bDeviceSubClass;
	public byte bDeviceProtocol;
	public byte bMaxPacketSize0;
	public short idVendor;
	public short idProduct;
	public short bcdDevice;
	public byte iManufacturer;
	public byte iProduct;
	public byte iSerialNumber;
	public byte bNumConfigurations;
}