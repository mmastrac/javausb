package com.grack.libusb.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface LibUSBNative extends Library {
	LibUSBNative INSTANCE = (LibUSBNative) Native.loadLibrary("usb-1.0.0", LibUSBNative.class);

	// int LIBUSB_CALL libusb_init(libusb_context **ctx);
	int libusb_init(PointerByReference ctx);

	// void LIBUSB_CALL libusb_set_debug(libusb_context *ctx, int level);
	void libusb_set_debug(Pointer ctx, int level);

	// void LIBUSB_CALL libusb_exit(libusb_context *ctx);
	void libusb_exit(Pointer ctx);

	// ssize_t LIBUSB_CALL libusb_get_device_list(libusb_context *ctx,
	// libusb_device ***list);
	int libusb_get_device_list(Pointer ctx, PointerByReference list);

	// void LIBUSB_CALL libusb_free_device_list(libusb_device **list, int
	// unref_devices);
	void libusb_free_device_list(Pointer list, boolean unref_devices);

	// void LIBUSB_CALL libusb_unref_device(libusb_device *dev);
	void libusb_unref_device(Pointer dev);

	// int LIBUSB_CALL libusb_get_device_descriptor(libusb_device *dev, struct
	// libusb_device_descriptor *desc);
	int libusb_get_device_descriptor(Pointer dev, libusb_device_descriptor[] desc);

	// int LIBUSB_CALL libusb_open(libusb_device *dev, libusb_device_handle
	// **handle);
	int libusb_open(Pointer dev, PointerByReference handle);

	// int LIBUSB_CALL libusb_get_config_descriptor(libusb_device *dev, uint8_t
	// config_index, struct libusb_config_descriptor **config);
	int libusb_get_config_descriptor(Pointer dev, int config_index, PointerByReference config);

	// void LIBUSB_CALL libusb_free_config_descriptor( struct libusb_config_descriptor *config);
	void libusb_free_config_descriptor(Pointer config);

	// void LIBUSB_CALL libusb_close(libusb_device_handle *dev_handle);
	void libusb_close(Pointer handle);

	// int LIBUSB_CALL libusb_get_string_descriptor_ascii(libusb_device_handle
	// *dev, uint8_t desc_index, unsigned char *data, int length);
	int libusb_get_string_descriptor_ascii(Pointer dev, byte desc_index, byte[] data, int length);

}
