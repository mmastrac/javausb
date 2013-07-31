javausb
=======

Java USB library.

Simple example:

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
