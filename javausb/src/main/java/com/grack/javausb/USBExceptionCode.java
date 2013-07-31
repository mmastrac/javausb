package com.grack.javausb;

public enum USBExceptionCode {
	LIBUSB_SUCCESS(0),

	LIBUSB_ERROR_IO(-1),

	LIBUSB_ERROR_INVALID_PARAM(-2),

	LIBUSB_ERROR_ACCESS(-3),

	LIBUSB_ERROR_NO_DEVICE(-4),

	LIBUSB_ERROR_NOT_FOUND(-5),

	LIBUSB_ERROR_BUSY(-6),

	LIBUSB_ERROR_TIMEOUT(-7),

	LIBUSB_ERROR_OVERFLOW(-8),

	LIBUSB_ERROR_PIPE(-9),

	LIBUSB_ERROR_INTERRUPTED(-10),

	LIBUSB_ERROR_NO_MEM(-11),

	LIBUSB_ERROR_NOT_SUPPORTED(-12),

	LIBUSB_ERROR_OTHER(-99),

	LIBUSB_ERROR_UNKNOWN(Integer.MIN_VALUE),

	;

	private int code;

	private USBExceptionCode(int code) {
		this.code = code;
	}

	public static USBExceptionCode lookup(int code) {
		// Not the most efficient lookup
		for (USBExceptionCode exceptionCode : values()) {
			if (exceptionCode.code == code)
				return exceptionCode;
		}
		return LIBUSB_ERROR_UNKNOWN;
	}

	public int getCode() {
		return code;
	}

}
