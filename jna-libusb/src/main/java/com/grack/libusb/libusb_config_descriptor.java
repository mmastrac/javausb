package com.grack.libusb;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.common.collect.ImmutableList;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class libusb_config_descriptor extends Structure {
	public libusb_config_descriptor() {
	}
	
	public libusb_config_descriptor(Pointer p) {
		super(p);
		read();
	}
	
	@Override
	protected List<String> getFieldOrder() {
		return ImmutableList.of("bLength", "bDescriptorType", "wTotalLength", "bNumInterfaces", "bConfigurationValue", "iConfiguration",
				"bmAttributes", "MaxPower", "interfaces", "extra", "extra_length");
	}

	public byte bLength;
	public byte bDescriptorType;
	public short wTotalLength;
	public byte bNumInterfaces;
	public byte bConfigurationValue;
	public byte iConfiguration;
	public byte bmAttributes;
	public byte MaxPower;
	public Pointer interfaces; // libusb_interface, length = bNumInterfaces
	public Pointer extra; // char*, length = extra_length
	public int extra_length;
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
