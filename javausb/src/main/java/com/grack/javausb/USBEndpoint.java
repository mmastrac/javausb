package com.grack.javausb;

import com.grack.javausb.jna.libusb_endpoint_descriptor;

public class USBEndpoint {
	private libusb_endpoint_descriptor descriptor;

	public USBEndpoint(libusb_endpoint_descriptor descriptor) {
		this.descriptor = descriptor;
	}

	public int address() {
		return descriptor.bEndpointAddress & 0xff;
	}

	public int number() {
		return descriptor.bEndpointAddress & 7;
	}
	
	public int maxPacketSize() {
		return descriptor.wMaxPacketSize & 0xffff;
	}

	public USBEndpointDirection direction() {
		return ((descriptor.bEndpointAddress & 0x80) == 0x80) ? USBEndpointDirection.IN : USBEndpointDirection.OUT;
	}

	public USBTransferType transferType() {
		return USBTransferType.values()[descriptor.bmAttributes & 3];
	}
	
	public int synchAddress() {
		return descriptor.bSynchAddress;
	}
	
	@Override
	public String toString() {
		return direction() + " " + transferType() + " endpoint #" + number() + " (packet size = 0x" + Integer.toHexString(maxPacketSize()) + ")";
	}
}
