package com.grack.libusb;

import com.grack.libusb.jna.libusb_endpoint_descriptor;

public class LibUSBEndpoint {
	private libusb_endpoint_descriptor descriptor;

	public LibUSBEndpoint(libusb_endpoint_descriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	public int address() {
		return descriptor.bEndpointAddress;
	}
	
	public int synchAddress() {
		return descriptor.bSynchAddress;
	}

}
