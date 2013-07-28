package com.grack.javausb;

import java.io.IOException;

import org.junit.Test;

import com.grack.javausb.USB;
import com.grack.javausb.USBConfiguration;
import com.grack.javausb.USBDevice;
import com.grack.javausb.USBException;
import com.grack.javausb.USBInterface;
import com.grack.javausb.USBInterfaceDescriptor;
import com.grack.javausb.USBOpenDevice;

/**
 * Simple {@link USB} "finalizer" tests.
 */
public class USBFinalizerTest {
	@Test
	public void listDevices() throws USBException, IOException, InterruptedException {
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
	private void listDevices_() throws USBException {
		USB usb = new USB();
		for (USBDevice device : usb.devices()) {
			System.out.println(device);
			try (USBOpenDevice open = device.open()) {
				try {
					System.out.println("Manufacturer: " + open.manufacturer());
				} catch (USBException e) {
					System.out.println("Error reading manufacturer: " + e.getError());
				}

				try {
					System.out.println("Product: " + open.product());
				} catch (USBException e) {
					System.out.println("Error reading product: " + e.getError());
				}

				try {
					System.out.println("Serial number: " + open.serialNumber());
				} catch (USBException e) {
					System.out.println("Error reading serial number: " + e.getError());
				}

				for (USBConfiguration configuration : device.configurations()) {
					System.out.println("  " + configuration);
					System.out.println("  Max power = " + configuration.maxPowerInMilliamps() + "mA");

					for (USBInterface iface : configuration.interfaces()) {
						System.out.println("    " + iface);
						for (USBInterfaceDescriptor ifaceDescriptor : iface.altSettings()) {
							System.out.println("      " + ifaceDescriptor);
							System.out.println("      " + "Class = " + ifaceDescriptor.interfaceClass());
							System.out.println("      " + "Subclass = " + ifaceDescriptor.subClass());
							System.out.println("      " + "Protocol = " + ifaceDescriptor.protocol());
							try {
								System.out.println("      " + "Description = " + ifaceDescriptor.description(open));
							} catch (USBException e) {
								System.out.println("Error reading interface description: " + e.getError());
							}
						}
					}
				}
			} catch (USBException e1) {
				System.out.println("Error opening device: " + e1.getError());
			}
			System.out.println();
		}

		usb = null;
	}

	@Test
	public void automaticallyFinalized() throws USBException, InterruptedException {
		USB usb = new USB();
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
