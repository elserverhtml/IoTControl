package com.example.lampcontrol;

import android.content.Intent;

abstract class Device {
    final static  int IMAGE_BT_CONNECTED = R.drawable.ic_bluetooth_connected;
    final static  int IMAGE_BT_DISCONNECTED = R.drawable.ic_bluetooth_disconnected;
    final static  int IMAGE_WF_CONNECTED = R.drawable.ic_wifi_connected;
    final static  int IMAGE_WF_DISCONNECTED = R.drawable.ic_wifi_disconnected;
    final static  int IMAGE_DEVICE_ON = R.drawable.ic_device_on;
    final static  int IMAGE_DEVICE_OFF = R.drawable.ic_device_off;

    private int image_connected;
    private int image_disconnected;
    private int image_deviceOn ;
    private int image_deviceOff;

    private String name;
    private boolean deviceIsOn = false;
    private boolean deviceIsConnected = false;

    private boolean typeConnectionIsBluetooth;
    private String BT_MAC = "";
    // TODO: wifi settings...

    Device(String name, boolean typeConnectionIsBluetooth) {
        this.name = name;
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;

        if(this.typeConnectionIsBluetooth) {
            this.image_connected = Device.IMAGE_BT_CONNECTED;
            this.image_disconnected = Device.IMAGE_BT_DISCONNECTED;
        } else {
            this.image_connected = Device.IMAGE_WF_CONNECTED;
            this.image_disconnected = Device.IMAGE_WF_DISCONNECTED;
        }

        this.image_deviceOn = Device.IMAGE_DEVICE_ON;
        this.image_deviceOff = Device.IMAGE_DEVICE_OFF;
    }

    Device(String name, boolean typeConnectionIsBluetooth, int image_deviceOn, int image_deviceOff) {
        this.name = name;
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;

        if(this.typeConnectionIsBluetooth) {
            this.image_connected = Device.IMAGE_BT_CONNECTED;
            this.image_disconnected = Device.IMAGE_BT_DISCONNECTED;
        } else {
            this.image_connected = Device.IMAGE_WF_CONNECTED;
            this.image_disconnected = Device.IMAGE_WF_DISCONNECTED;
        }

        this.image_deviceOn = image_deviceOn;
        this.image_deviceOff = image_deviceOff;
    }

    abstract void isConnectorOn();
    abstract void findDevices();
    abstract void connectDevice();
    abstract Intent deviceMenu(Intent intent);

    void setName(String name){
        this.name = name;
    }
    String getName(){
        return this.name;
    }

    boolean isOn() {
        return deviceIsOn;
    }
    void setDeviceIsOn(boolean isOn) {
        this.deviceIsOn = isOn;
    }

    boolean isConnected() {
        return deviceIsConnected;
    }
    void setDeviceConnected(boolean isOnline) {
        this.deviceIsConnected = isOnline;
    }

    boolean isTypeConnectionBluetooth() {
        return typeConnectionIsBluetooth;
    }
    void setTypeConnection(boolean typeConnectionIsBluetooth) {
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;
    }

    String getBT_MAC() {
        return BT_MAC;
    }
    void setBT_MAC(String BT_MAC) {
        this.BT_MAC = BT_MAC;
    }

    int getImageConnected() {
        return image_connected;
    }
    void setImageConnected(int image_connected) {
        this.image_connected = image_connected;
    }

    int getImageDisconnected() {
        return image_disconnected;
    }
    void setImageDisconnected(int image_disconnected) {
        this.image_disconnected = image_disconnected;
    }

    int getImageDeviceOn() {
        return image_deviceOn;
    }
    void setImageDeviceOn(int image_deviceOn) {
        this.image_deviceOn = image_deviceOn;
    }

    int getImageDeviceOff() {
        return image_deviceOff;
    }
    void setImageDeviceOff(int image_deviceOff) {
        this.image_deviceOff = image_deviceOff;
    }
}
