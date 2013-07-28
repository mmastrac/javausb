package com.grack.javausb;

import com.grack.javausb.jna.libusb_endpoint_descriptor;

public class USBEndpoint {
	private libusb_endpoint_descriptor descriptor;

	public USBEndpoint(libusb_endpoint_descriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	public int address() {
		return descriptor.bEndpointAddress;
	}
	
	public int synchAddress() {
		return descriptor.bSynchAddress;
	}

}
