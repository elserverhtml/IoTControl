package com.example.lampcontrol;

abstract class Device {
    private int image_connected;
    private int image_disconnected;
    private int image_deviceOn ;
    private int image_deviceOff;

    private String name;
    private boolean deviceIsOn = false;
    private boolean deviceIsConnected = false;

    private boolean typeConnectionIsBluetooth;
    private String BT_MAC = "";
    // wifi settings...

    Device(String name, boolean typeConnectionIsBluetooth) {
        this.name = name;
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;

        if(this.typeConnectionIsBluetooth) {
            this.image_connected = R.drawable.ic_bluetooth_connected;
            this.image_disconnected = R.drawable.ic_bluetooth_disconnected;
        } else {
            this.image_connected = R.drawable.ic_wifi_connected;
            this.image_disconnected = R.drawable.ic_wifi_disconnected;
        }

        this.image_deviceOn = R.drawable.ic_device_on;
        this.image_deviceOff = R.drawable.ic_device_off;
    }

    Device(String name, boolean typeConnectionIsBluetooth, int image_deviceOn, int image_deviceOff) {
        this.name = name;
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;

        if(this.typeConnectionIsBluetooth) {
            this.image_connected = R.drawable.ic_bluetooth_connected;
            this.image_disconnected = R.drawable.ic_bluetooth_disconnected;
        } else {
            this.image_connected = R.drawable.ic_wifi_connected;
            this.image_disconnected = R.drawable.ic_wifi_disconnected;
        }

        this.image_deviceOn = image_deviceOn;
        this.image_deviceOff = image_deviceOff;
    }

    abstract void isConnectorOn();
    abstract void findDevices();
    abstract void connectDevice();
    abstract void deviceMenu();

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
