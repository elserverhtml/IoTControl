package com.example.IoTControl;

import java.util.ArrayList;
import java.util.List;

abstract class Device {
    private final static  int IMAGE_BT_CONNECTED = R.drawable.ic_bluetooth_connected;
    private final static  int IMAGE_BT_DISCONNECTED = R.drawable.ic_bluetooth_disconnected;
    private final static  int IMAGE_WF_CONNECTED = R.drawable.ic_wifi_connected;
    private final static  int IMAGE_WF_DISCONNECTED = R.drawable.ic_wifi_disconnected;
    final static  int IMAGE_DEVICE_ON = R.drawable.ic_device_on;
    final static  int IMAGE_DEVICE_OFF = R.drawable.ic_device_off;

    final static int DEVICE_STATUS_WAITING = 0;
    final static int DEVICE_STATUS_ONLINE = 2;
    final static int DEVICE_STATUS_OFFLINE = 1;

    private int image_connected;
    private int image_disconnected;
    private int image_deviceOn ;
    private int image_deviceOff;

    private int deviceId;
    private String name;
    private boolean deviceIsOn = false;
    private int connectionStatus = DEVICE_STATUS_WAITING;
    private List<Timer> timers = new ArrayList<>();

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
    abstract void connectDevice();

    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

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

    int getConnectionStatus() {
        return connectionStatus;
    }
    void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    boolean isTypeConnectionBluetooth() {
        return typeConnectionIsBluetooth;
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

    List<Timer> getAllTimers() {
        return timers;
    }
    void addTimer(Timer timer) {
        timers.add(timer);
    }
    void removeTimer(Timer timer) {
        timers.remove(timer);
    }
    void removeTimer(int id) {
        timers.remove(id);
    }
}
