package com.grack.javausb;

public class USBException extends Exception {
	private static final long serialVersionUID = 1L;
	private int error;
	private USBExceptionCode code;

	public USBException(int error) {
		super();
		this.code = USBExceptionCode.lookup(error);
		this.error = error;
	}

	@Override
	public String getMessage() {
		return "LibUSB error: " + error + " (" + code + ")";
	}
	
	public USBExceptionCode getCode() {
		return code;
	}
	
	public int getError() {
		return error;
	}
}
