package com.example.lampcontrol;

import java.util.UUID;

class BTDevice extends Device{
    final static private String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    final static private UUID myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

    BTDevice(String name, boolean typeConnectionIsBluetooth) {
        super(name, typeConnectionIsBluetooth);
    }

    BTDevice(String name, boolean typeConnectionIsBluetooth, int image_deviceOn, int image_deviceOff) {
        super(name, typeConnectionIsBluetooth, image_deviceOn, image_deviceOff);
    }

    @Override
    void isConnectorOn() {

    }

    @Override
    void findDevices() {

    }

    @Override
    void connectDevice() {

    }

    @Override
    void deviceMenu() {

    }
}
