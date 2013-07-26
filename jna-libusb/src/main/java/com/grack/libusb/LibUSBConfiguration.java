package com.grack.libusb;

public class LibUSBConfiguration {
	private libusb_config_descriptor descriptor;

	public LibUSBConfiguration(libusb_config_descriptor descriptor) {
		this.descriptor = descriptor;
	}

	public int maxPowerInMilliamps() {
		return (descriptor.MaxPower & 0xff) * 2;
	}
	
	public int numInterfaces() {
		return descriptor.bNumInterfaces;
	}
}
