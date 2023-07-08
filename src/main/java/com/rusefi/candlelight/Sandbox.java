package com.rusefi.candlelight;


import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class Sandbox {

    /**
     * Main method.
     *
     * @param args Command-line arguments (Ignored)
     */
    public static void main(final String[] args) throws UsbException {
        final UsbServices services = UsbHostManager.getUsbServices();
        System.out.println("USB Service Implementation: "
                + services.getImpDescription());
        System.out.println("Implementation version: "
                + services.getImpVersion());
        System.out.println("Service API version: " + services.getApiVersion());
        System.out.println();


        processDevice(services.getRootUsbHub());
    }

    private static void processDevice(UsbDevice device) {

        if (device.isUsbHub()) {
            UsbHub hub = (UsbHub) device;
            for (UsbDevice child : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
                processDevice(child);
            }
        } else {
            // When device is not a hub then dump its name.

            try {
                UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
                if (descriptor.idVendor() == DeviceLocator.VENDOR_ID && descriptor.idProduct() == DeviceLocator.PRODUCT_ID) {
                    System.out.println("Happy " + device);

                    haveFun(device);

                }
            } catch (Exception e) {
                // On Linux this can fail because user has no write permission
                // on the USB device file. On Windows it can fail because
                // no libusb device driver is installed for the device
                System.err.println("Ignoring problematic device: " + e);
            }
        }
    }

    private static void haveFun(UsbDevice device) throws UsbException, UnsupportedEncodingException {
        System.out.println("Having fun with " + device);


        UsbConfiguration configuration = device.getUsbConfiguration((byte) 1);
        System.out.println("Got " + configuration + " " + configuration.getUsbInterfaces());

        UsbInterface iface = configuration.getUsbInterface((byte) 0);
        iface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });

        System.out.println("Claimed! " + iface.getInterfaceString());


        UsbControlIrp irp = device.createUsbControlIrp(
                /*bmRequestType*/
                (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN
                        | UsbConst.REQUESTTYPE_TYPE_VENDOR
                        | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
                /*bRequest*/
                (byte) 4,
                /*wValue*/ (short) 0,
                /*wIndex*/ (short) 0
        );
        byte[] bytes = new byte[40];
        irp.setData(bytes);
        device.syncSubmit(irp);
        System.out.println(Arrays.toString(bytes));
    }
}
