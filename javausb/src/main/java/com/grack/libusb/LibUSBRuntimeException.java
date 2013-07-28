package com.grack.libusb;

public class LibUSBRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public LibUSBRuntimeException(LibUSBException e) {
		super(e);
	}
}
