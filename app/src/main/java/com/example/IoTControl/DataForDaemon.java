package com.example.IoTControl;

import android.content.Context;
import android.util.Log;

import java.util.ArrayDeque;

class DataForDaemon {
    private ArrayDeque<DeviceInfo> deviceArrayDeque = new ArrayDeque<>();
    private ArrayDeque<String> commandForActivityDevice = new ArrayDeque<>();

    DeviceInfo getDeviceFromQueue() {
        return deviceArrayDeque.pollFirst();
    }

    void addDevice(DeviceInfo device) {
        device.isActivityDevice = false;
        deviceArrayDeque.addLast(device);
        Log.d("Data", "Устройство добавлено");
    }

    void addActivityDevice(DeviceInfo device) {
        device.isActivityDevice = true;
        deviceArrayDeque.addFirst(device);
    }

    String getCommandForActivityDevice() {
        if(!commandForActivityDevice.isEmpty())
            return commandForActivityDevice.pollFirst();
        else return null;
    }

    void addCommandForActivityDevice(String string) {
        commandForActivityDevice.addLast(string);
    }

    boolean isEmpty() {
        return deviceArrayDeque.isEmpty();
    }

    static class DeviceInfo {
        int position; // or id
        Context parentContext;
        boolean isActivityDevice;
        boolean isSet;
        boolean isOnLamp;
    }
}
