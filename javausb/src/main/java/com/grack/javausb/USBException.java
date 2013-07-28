package com.grack.javausb;

public class USBException extends Exception {
	private static final long serialVersionUID = 1L;
	private int error;

	public USBException(int error) {
		super("LibUSB error: " + error);
		this.error = error;
	}
	
	public int getError() {
		return error;
	}
}
