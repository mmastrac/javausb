package com.grack.javausb;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import com.sun.jna.Pointer;

public class USBBulkEndpointInputStream extends InputStream implements AutoCloseable, Closeable {
	private Pointer handle;
	private int endpoint;

	USBBulkEndpointInputStream(Pointer handle, int endpoint) {
		this.handle = handle;
		this.endpoint = endpoint;
	}
	
	@Override
	public int read() throws IOException {
		byte[] data = new byte[1];
		int result = read(data);
		if (result <= 0)
			return result;
		
		return data[0];
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			return USBNative.bulkTransfer(handle, endpoint, b, off, len);
		} catch (USBException e) {
			if (e.getCode() == USBExceptionCode.LIBUSB_ERROR_TIMEOUT)
				return 0;
			throw new IOException(e);
		}
	}
	
	@Override
	public void close() {
		// Technically nothing happens here
	}
}
