package com.example.IoTControl;

import android.content.Context;
import android.util.Log;

import java.util.ArrayDeque;

class DataForDaemon {
    private final ArrayDeque<DeviceInfo> deviceArrayDeque = new ArrayDeque<>();
    private ArrayDeque<String> commandForActivityDevice = new ArrayDeque<>();

    DeviceInfo getDeviceFromQueue() {
        synchronized (deviceArrayDeque) {
            if (!deviceArrayDeque.isEmpty()) return deviceArrayDeque.pollFirst();
            else return null;
        }
    }

    void addDevice(DeviceInfo device) {
        synchronized (deviceArrayDeque) {
            deviceArrayDeque.addLast(device);
        }
        Log.d("Data", "Устройство добавлено");
    }

    String getCommandForActivityDevice() {
        if(!commandForActivityDevice.isEmpty())
            return commandForActivityDevice.pollFirst();
        else return null;
    }

    void setCommandForActivityDevice(String commandForActivityDevice) {
        this.commandForActivityDevice.addLast(commandForActivityDevice);
    }

    void clearCommands() {
        synchronized (deviceArrayDeque) {
            this.commandForActivityDevice.clear();
        }
    }

    void isDeviceInDeque(DeviceInfo deviceInfo) {
        synchronized (deviceArrayDeque) {
            for (DataForDaemon.DeviceInfo selectedDevice : deviceArrayDeque) {
                if (selectedDevice.position == deviceInfo.position) {
                    MainActivity.devices.get(deviceInfo.position).checkingDevice(false);
                    deviceArrayDeque.remove(selectedDevice);
                    MainActivity.daemon.setActivityDevice(selectedDevice);
                }
            }
            MainActivity.daemon.checkDevice(deviceInfo.position);
        }
    }

    static class DeviceInfo {
        int position;
        Context parentContext;
        boolean isSet;
        boolean isOnLamp;
    }
}
