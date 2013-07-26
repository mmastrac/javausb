package com.grack.libusb;

import java.io.IOException;

import org.junit.Test;

/**
 * Simple {@link LibUSB} tests.
 */
public class LibUSBTest {
	@Test
	public void listDevices() throws LibUSBException, IOException {
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
				
				System.out.println("Number of configurations = " + device.numConfigurations());

				for (LibUSBConfiguration configuration : device.configurations()) {
					System.out.println("Max power = " + configuration.maxPowerInMilliamps() + "mA");
					System.out.println("Number of interfaces = " + configuration.numInterfaces());
				}
				
				System.out.println();
			} catch (LibUSBException e1) {
				System.out.println("Error opening device: " + e1.getError());
			}
		}
		
		System.gc();
		System.in.read();
	}
}
