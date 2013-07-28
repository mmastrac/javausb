package com.grack.javausb;

public class USBRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public USBRuntimeException(USBException e) {
		super(e);
	}
}
