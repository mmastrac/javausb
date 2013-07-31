package com.grack.javausb;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.jna.Pointer;

public class USBBulkEndpointOutputStream extends OutputStream {
	private USB usb;
	private Pointer handle;
	private int endpoint;

	USBBulkEndpointOutputStream(USB usb, Pointer handle, int endpoint) {
		this.usb = usb;
		this.handle = handle;
		this.endpoint = endpoint;
	}

	@Override
	public void write(int b) throws IOException {
		write(new byte[] { (byte) b });
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		try {
			usb.bulkWrite(handle, endpoint, b, off, len);
		} catch (USBException e) {
			throw new IOException(e);
		}
	};
}
