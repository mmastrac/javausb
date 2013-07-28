package com.grack.libusb;

public class LibUSBException extends Exception {
	private static final long serialVersionUID = 1L;
	private int error;

	public LibUSBException(int error) {
		super("LibUSB error: " + error);
		this.error = error;
	}
	
	public int getError() {
		return error;
	}
}
