package com.grack.javausb;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;

public class USBOpenDevice implements AutoCloseable, Closeable {
	private USBDevice device;
	private Pointer handle;
	private USB usb;
	private FinalizerReference finalizer;

	private static final Logger logger = Logger.getLogger(USBDevice.class.getName());

	public USBOpenDevice(USB usb, USBDevice device, Pointer handle) {
		this.usb = usb;
		this.device = device;
		this.handle = handle;
		
		finalizer = usb.trackFinalizer(this, new LibUSBOpenDeviceFinalizer(handle));
	}

	private static class LibUSBOpenDeviceFinalizer implements Finalizer {
		private Pointer handle;

		public LibUSBOpenDeviceFinalizer(Pointer handle) {
			this.handle = handle;
		}

		@Override
		public void cleanup() {
			logger.info("Cleanup: open device");
			USBNative.closeDevice(handle);
		}
	}

	public String getStringDescriptionAscii(int index) throws USBException {
		return USBNative.getStringDescriptionAscii(handle, (byte)index);
	}
	
	public String manufacturer() throws USBException {
		if (device.descriptor.iManufacturer == 0)
			return null;

		return USBNative.getStringDescriptionAscii(handle, device.descriptor.iManufacturer);
	}

	public String product() throws USBException {
		if (device.descriptor.iProduct == 0)
			return null;

		return USBNative.getStringDescriptionAscii(handle, device.descriptor.iProduct);
	}

	public String serialNumber() throws USBException {
		if (device.descriptor.iSerialNumber == 0)
			return null;

		return USBNative.getStringDescriptionAscii(handle, device.descriptor.iSerialNumber);
	}

	public InputStream openBulkReadEndpoint(USBEndpoint endpoint) {
		Preconditions.checkArgument(endpoint.direction() == USBEndpointDirection.IN);
		return new USBBulkEndpointInputStream(handle, endpoint.address());
	}

	public OutputStream openBulkWriteEndpoint(USBEndpoint endpoint) {
		Preconditions.checkArgument(endpoint.direction() == USBEndpointDirection.OUT);
		return new USBBulkEndpointOutputStream(handle, endpoint.address());
	}
	
	@Override
	public synchronized void close() {
		if (finalizer != null) {
			usb.forceFinalization(finalizer);
			finalizer = null;
			handle = null;
			usb = null;
		}
	}

	public void activate(USBConfiguration config) throws USBException {
		USBNative.setConfiguration(handle, config.number());
	}

	public void claim(USBInterface config) throws USBException {
		USBNative.claimInterface(handle, config.number());
	}
	
	public int sendControlTransfer(int requestType, byte request, int value, int index, byte[] buffer)
			throws USBException {
		return sendControlTransfer(requestType, request, value, index, buffer, 0, buffer.length);
	}

	public int sendControlTransfer(int requestType, byte request, int value, int index, byte[] buffer, int offset, int length)
			throws USBException {
		return USBNative.sendControlTransfer(handle, requestType, request, value, index, buffer, offset, length);
	}
}
