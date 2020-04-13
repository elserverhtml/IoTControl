package com.example.IoTControl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

abstract class Device {
    private final static  int IMAGE_BT_CONNECTED = R.drawable.ic_bluetooth_connected;
    private final static  int IMAGE_BT_CONNECT_UNKNOWN = R.drawable.ic_bluetooth_unknown;
    private final static  int IMAGE_BT_DISCONNECTED = R.drawable.ic_bluetooth_disconnected;
    private final static  int IMAGE_WF_CONNECT_UNKNOWN = R.drawable.ic_wifi_unknown;
    private final static  int IMAGE_WF_CONNECTED = R.drawable.ic_wifi_connected;
    private final static  int IMAGE_WF_DISCONNECTED = R.drawable.ic_wifi_disconnected;
    final static  int IMAGE_DEVICE_ON = R.drawable.ic_device_on;
    final static  int IMAGE_DEVICE_OFF = R.drawable.ic_device_off;

    final static int DEVICE_STATUS_WAITING = 0;
    final static int DEVICE_STATUS_ONLINE = 2;
    final static int DEVICE_STATUS_OFFLINE = 1;

    final static int DEVICE_ON = 1;
    final static int DEVICE_OFF = 0;
    final static int DEVICE_ON_OR_OFF = -1;

    private int imageConnected;
    private int imageConnectUnknown;
    private int imageDisconnected;
    private int imageDeviceOn;
    private int imageDeviceOff;

    private int position;
    private String name;
    private int deviceIsOn = -1;
    private int connectionStatus = DEVICE_STATUS_WAITING;
    private List<Timer> timers = new ArrayList<>();
    private boolean isChecking = false;
    private boolean typeConnectionIsBluetooth;

    private String BT_MAC = "";
    // TODO: wifi settings...

    Device(int position, String name, boolean typeConnectionIsBluetooth) {
        this(position, name, typeConnectionIsBluetooth, IMAGE_DEVICE_ON, IMAGE_DEVICE_OFF);
    }

    Device(int position, String name, boolean typeConnectionIsBluetooth, int imageDeviceOn, int imageDeviceOff) {
        this.position = position;
        this.name = name;
        this.typeConnectionIsBluetooth = typeConnectionIsBluetooth;

        if(this.typeConnectionIsBluetooth) {
            this.imageConnected = Device.IMAGE_BT_CONNECTED;
            this.imageConnectUnknown = Device.IMAGE_BT_CONNECT_UNKNOWN;
            this.imageDisconnected = Device.IMAGE_BT_DISCONNECTED;
        } else {
            this.imageConnected = Device.IMAGE_WF_CONNECTED;
            this.imageConnectUnknown = Device.IMAGE_WF_CONNECT_UNKNOWN;
            this.imageDisconnected = Device.IMAGE_WF_DISCONNECTED;
        }

        this.imageDeviceOn = imageDeviceOn;
        this.imageDeviceOff = imageDeviceOff;
    }

    abstract void isCanConnect(Context context);
    abstract void controlDeviceFromList(Context context, boolean isOn);
    abstract void connectToDevice(Context context);

    int getPosition() {
        return position;
    }
    void setPosition(int position) {
        this.position = position;
    }

    void setName(String name){
        this.name = name;
    }
    String getName(){
        return this.name;
    }

    int isOn() {
        return deviceIsOn;
    }
    void setDeviceIsOn(int isOn) {
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
        return imageConnected;
    }
    void setImageConnected(int image_connected) {
        this.imageConnected = image_connected;
    }

    int getImageConnectUnknown() {
        return imageConnectUnknown;
    }
    void setImageConnectUnknown(int imageConnectUnknown) {
        this.imageConnectUnknown = imageConnectUnknown;
    }

    int getImageDisconnected() {
        return imageDisconnected;
    }
    void setImageDisconnected(int image_disconnected) {
        this.imageDisconnected = image_disconnected;
    }

    int getImageDeviceOn() {
        return imageDeviceOn;
    }
    void setImageDeviceOn(int image_deviceOn) {
        this.imageDeviceOn = image_deviceOn;
    }

    int getImageDeviceOff() {
        return imageDeviceOff;
    }
    void setImageDeviceOff(int image_deviceOff) {
        this.imageDeviceOff = image_deviceOff;
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

    void checkingDevice(boolean isChecking) {
        this.isChecking = isChecking;
    }
    boolean isChecking() {
        return this.isChecking;
    }
}
