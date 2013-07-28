package com.grack.libusb;

import java.io.IOException;

import org.junit.Test;

/**
 * Simple {@link LibUSB} tests.
 */
public class LibUSBFinalizerTest {
	@Test
	public void listDevices() throws LibUSBException, IOException, InterruptedException {
		listDevices_();

		System.gc();
		Thread.sleep(1000);
		System.gc();
		Thread.sleep(1000);
		System.gc();
		Thread.sleep(1000);

		// Heap dump at this point
		// System.in.read();
	}

	/**
	 * This is kept in a separate function to avoid the locals from keeping
	 * ojbects alive.
	 */
	private void listDevices_() throws LibUSBException {
		LibUSB usb = new LibUSB();
		for (LibUSBDevice device : usb.devices()) {
			System.out.println(device);
			try (LibUSBOpenDevice open = device.open()) {
				try {
					System.out.println("Manufacturer: " + open.manufacturer());
				} catch (LibUSBException e) {
					System.out.println("Error reading manufacturer: " + e.getError());
				}

				try {
					System.out.println("Product: " + open.product());
				} catch (LibUSBException e) {
					System.out.println("Error reading product: " + e.getError());
				}

				try {
					System.out.println("Serial number: " + open.serialNumber());
				} catch (LibUSBException e) {
					System.out.println("Error reading serial number: " + e.getError());
				}

				for (LibUSBConfiguration configuration : device.configurations()) {
					System.out.println("  " + configuration);
					System.out.println("  Max power = " + configuration.maxPowerInMilliamps() + "mA");

					for (LibUSBInterface iface : configuration.interfaces()) {
						System.out.println("    " + iface);
						for (LibUSBInterfaceDescriptor ifaceDescriptor : iface.altSettings()) {
							System.out.println("      " + ifaceDescriptor);
							System.out.println("      " + "Class = " + ifaceDescriptor.interfaceClass());
							System.out.println("      " + "Subclass = " + ifaceDescriptor.subClass());
							System.out.println("      " + "Protocol = " + ifaceDescriptor.protocol());
							try {
								System.out.println("      " + "Description = " + ifaceDescriptor.description(open));
							} catch (LibUSBException e) {
								System.out.println("Error reading interface description: " + e.getError());
							}
						}
					}
				}
			} catch (LibUSBException e1) {
				System.out.println("Error opening device: " + e1.getError());
			}
			System.out.println();
		}

		usb = null;
	}

	@Test
	public void automaticallyFinalized() throws LibUSBException, InterruptedException {
		LibUSB usb = new LibUSB();
		usb.devices();
		usb = null;

		System.gc();
		Thread.sleep(1000);
		System.gc();
		Thread.sleep(1000);
		System.gc();
		Thread.sleep(1000);
	}
}
