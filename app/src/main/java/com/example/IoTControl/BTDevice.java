package com.example.IoTControl;

import android.content.Context;

class BTDevice extends Device{

    BTDevice(String name, boolean typeConnectionIsBluetooth, String BT_MAC) {
        super(name, typeConnectionIsBluetooth);
        super.setBT_MAC(BT_MAC);
    }

    BTDevice(String name, boolean typeConnectionIsBluetooth, String BT_MAC, int image_deviceOn, int image_deviceOff) {
        super(name, typeConnectionIsBluetooth, image_deviceOn, image_deviceOff);
        super.setBT_MAC(BT_MAC);
    }

    @Override
    void isCanConnect(Context context, int pos) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = pos;
        deviceInfo.parentContext = context;
        deviceInfo.isSet = false;
        MainActivity.data.addDevice(deviceInfo);
    }

    @Override
    void controlDeviceFromList(Context context, int pos, boolean isOn) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = pos;
        deviceInfo.parentContext = context;
        deviceInfo.isSet = true;
        deviceInfo.isOnLamp = isOn;
        MainActivity.data.addDevice(deviceInfo);
    }

    @Override
    void connectToDevice(Context context, int pos) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = pos;
        deviceInfo.parentContext = context;
        MainActivity.data.addActivityDevice(deviceInfo);
    }

    public static class SendCommands {
        static boolean getStatusDevice(String string) {
            string = "gSD;";
            return true;
        }

        static boolean controlDevice(String string, boolean isOn) {
            string = "cD-";
            if(isOn) string = string + "1;";
            else string = string + "0;";
            return false;
        }

        static boolean getCurrentTime(String string) {
            string = "gCT";
            return true;
        }

        static boolean setCurrentTime(String string, int seconds, int minutes, int hours, int day, int month, int year, int weekday) {
            string = "sCT-" + seconds + "-"  + minutes + "-" + hours + "-" + day + "-" + month + "-" + year + "-" + weekday + ";";
            return false;
        }

        static boolean getAmountTimers(String string) {
            string = "gAT;";
            return true;
        }

        static boolean getTimer(String string, int num) {
            string = "gT-" + num + ";";
            return true;
        }

        static boolean addTimer(String string, int hours, int minutes, int weekdays, boolean isOn, boolean isEnable) {
            string = "aT-" + hours + "-" + minutes + "-" + weekdays + "-";
            if(isOn) string = string + "1-";
            else string = string + "0-";
            if(isEnable) string = string + "1;";
            else string = string + "0;";
            return false;
        }

        static boolean editTimer(String string, int num, int hours, int minutes, int weekdays, boolean isOn, boolean isEnable) {
            string = "aT-" + num + "-" + hours + "-" + minutes + "-" + weekdays + "-";
            if(isOn) string = string + "1-";
            else string = string + "0-";
            if(isEnable) string = string + "1;";
            else string = string + "0;";
            return false;
        }

        static boolean controlTimer(String string, int num, boolean isEnable) {
            string = "cT-" + num + "-";
            if(isEnable) string = string + "1;";
            else string = string + "0;";
            return false;
        }

        static boolean deleteTimer(String string, int num) {
            string = "cT-" + num + ";";
            return false;
        }
    }

    public static class GetData {
        static boolean statusDevice(String string) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // sD-1;
            return sb.substring(3,4).equals("1");
        }

        static String deviceTime(String s) {
            String string;
            StringBuilder sb = new StringBuilder();
            sb.append(s);
            // dT-60-60-24-31-12-99-7;
            string = sb.substring(9,11) + ":";
            string = string +  sb.substring(6,8) + ":";
            string = string +  sb.substring(3,5) + "  ";
            string = string +  sb.substring(12,14) + ".";
            string = string +  sb.substring(15,17) + ".";
            string = string +  sb.substring(18,20) + "  ";
            if(sb.substring(21,22).equals("0")) string = string + "ВС";
            else if(sb.substring(21,22).equals("1")) string = string + "ПН";
            else if(sb.substring(21,22).equals("2")) string = string + "ВТ";
            else if(sb.substring(21,22).equals("3")) string = string + "СР";
            else if(sb.substring(21,22).equals("4")) string = string + "ЧТ";
            else if(sb.substring(21,22).equals("5")) string = string + "ПТ";
            else if(sb.substring(21,22).equals("6")) string = string + "СБ";
            return string;
        }

        static int amountTimers(String string) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // aTs-20;
            return Integer.parseInt(sb.substring(4,6));
        }

        static Timer createTimer(String string) {
            int num, hours, minutes, weekdays;
            boolean isOn, isEnable;
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // sT-20-24-60-127-1-1;
            num = Integer.parseInt(sb.substring(3,5));
            hours = Integer.parseInt(sb.substring(6,8));
            minutes = Integer.parseInt(sb.substring(9,11));
            weekdays = Integer.parseInt(sb.substring(12,15));
            isOn = sb.substring(16,17).equals("1");
            isEnable = sb.substring(18,19).equals("1");
            return new Timer(num, weekdays, hours, minutes, isOn, isEnable);
        }
    }
}