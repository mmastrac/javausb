package com.grack.javausb;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Charsets;

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
	 * objects alive.
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

							for (USBEndpoint endpoint : ifaceDescriptor.endpoints()) {
								System.out.println("        " + "Number = " + endpoint.number());
								System.out.println("        " + "Direction = " + endpoint.direction());
								System.out.println("        " + "Transfer type = " + endpoint.transferType());
								System.out.println("        " + "Address = 0x" + Integer.toHexString(endpoint.address()));
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

	@Test
	public void xprotolab() throws USBException, IOException {
		USB usb = new USB();
		for (USBDevice device : usb.devices()) {
			if (device.vendor() == 0x16d0 && device.product() == 0x06f9) {
				System.out.println("Found XScope: " + device);

				for (USBConfiguration config : device.configurations()) {
					try (USBOpenDevice openDevice = device.open()) {
						System.out.println("Activating " + config);
						openDevice.activate(config);

						for (USBInterface iface : config.interfaces()) {
							System.out.println(iface);
							openDevice.claim(iface);
							for (USBInterfaceDescriptor ifaceDescriptor : iface.altSettings()) {
								for (USBEndpoint endpoint : ifaceDescriptor.endpoints()) {
									System.out.println(endpoint);
								}
							}
						}

						System.out.println("Requesting firmware version...");
						byte[] data = new byte[1024];
						int n = openDevice.sendControlTransfer(0xc0, (byte)'a', 0, 0, data);
						System.out.println("Version: " + new String(data, 0, n, Charsets.US_ASCII));
					}
				}
			}
		}
	}
}
