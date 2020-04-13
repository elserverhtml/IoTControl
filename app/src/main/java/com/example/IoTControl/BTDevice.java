package com.example.IoTControl;

import android.content.Context;

class BTDevice extends Device{

    BTDevice(int position, String name, boolean typeConnectionIsBluetooth, String BT_MAC) {
        super(position, name, typeConnectionIsBluetooth);
        super.setBT_MAC(BT_MAC);
    }

    BTDevice(int position, String name, boolean typeConnectionIsBluetooth, String BT_MAC, int image_deviceOn, int image_deviceOff) {
        super(position, name, typeConnectionIsBluetooth, image_deviceOn, image_deviceOff);
        super.setBT_MAC(BT_MAC);
    }

    @Override
    void isCanConnect(Context context) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = this.getPosition();
        deviceInfo.parentContext = context;
        deviceInfo.isSet = false;
        MainActivity.data.addDevice(deviceInfo);
    }

    @Override
    void controlDeviceFromList(Context context, boolean isOn) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = this.getPosition();
        deviceInfo.parentContext = context;
        deviceInfo.isSet = true;
        deviceInfo.isOnLamp = isOn;
        MainActivity.data.addDevice(deviceInfo);
    }

    @Override
    void connectToDevice(Context context) {
        DataForDaemon.DeviceInfo deviceInfo = new DataForDaemon.DeviceInfo();
        deviceInfo.position = this.getPosition();
        deviceInfo.parentContext = context;
        if(MainActivity.devices.get(this.getPosition()).isChecking()) MainActivity.data.isDeviceInDeque(deviceInfo);
        else MainActivity.daemon.setActivityDevice(deviceInfo);
    }

    static class SendCommands {
        static String getStatusDevice() {
            return "gSD;";
        }

        static String setStatusDevice(boolean isOn) {
            // sSD-1;
            String string = "sSD-";

            if(isOn) string = string + "1;";
            else string = string + "0;";

            return string;
        }

        static String getCurrentTime() {
            return "gCT;";
        }

        static String setCurrentTime(int seconds, int minutes, int hours, int day, int month, int year, int weekday) {
            // sCT-60-60-24-31-12-99-7;
            String string = "sCT-";

            if(seconds < 10) string = string + "0" + seconds;
            else string = string + seconds;
            string = string + "-";

            if(minutes < 10) string = string + "0" + minutes;
            else string = string + minutes;
            string = string + "-";

            if(hours < 10) string = string + "0" + hours;
            else string = string + hours;
            string = string + "-";

            if(day < 10) string = string + "0" + day;
            else string = string + day;
            string = string + "-";

            if(month < 10) string = string + "0" + month;
            else string = string + month;
            string = string + "-";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Integer.toString(year));
            string = string + stringBuilder.substring(2, 4);
            string = string + "-";

            string = string + weekday + ";";

            return string;
        }

        static String getAmountTimers() {
            return "gAT;";
        }

        static String getTimer(int num) {
            // gT-20;
            String string = "gT-";

            if(num < 10) string = string + "0" + num;
            else string = string + num;
            string = string + ";";

            return string;
        }

        static String addTimer(int hours, int minutes, int weekdays, boolean isOn, boolean isEnable) {
            // aT-24-60-127-1-1;
            String string = "aT-";

            if(hours < 10) string = string + "0" + hours;
            else string = string + hours;
            string = string + "-";

            if(minutes < 10) string = string + "0" + minutes;
            else string = string + minutes;
            string = string + "-";

            if(weekdays < 10) string = string + "00" + weekdays;
            else if(weekdays < 100) string = string + "0" + weekdays;
            else string = string + weekdays;
            string = string + "-";

            if(isOn) string = string + "1-";
            else string = string + "0-";

            if(isEnable) string = string + "1;";
            else string = string + "0;";

            return string;
        }

        static String editTimer(int num, int hours, int minutes, int weekdays, boolean isOn, boolean isEnable) {
            // eT-20-24-60-127-1-1;
            String string = "eT-";

            if(num < 10) string = string + "0" + num;
            else string = string + num;
            string = string + "-";

            if(hours < 10) string = string + "0" + hours;
            else string = string + hours;
            string = string + "-";

            if(minutes < 10) string = string + "0" + minutes;
            else string = string + minutes;
            string = string + "-";

            if(weekdays < 10) string = string + "00" + weekdays;
            else if(weekdays < 100) string = string + "0" + weekdays;
            else string = string + weekdays;
            string = string + "-";

            if(isOn) string = string + "1-";
            else string = string + "0-";

            if(isEnable) string = string + "1;";
            else string = string + "0;";

            return string;
        }

        static String manageTimer(int num, boolean isEnable) {
            //mT-20-1;
            String string = "mT-";

            if(num < 10) string = string + "0" + num;
            else string = string + num;
            string = string + "-";

            if(isEnable) string = string + "1;";
            else string = string + "0;";

            return string;
        }

        static String deleteTimer(int num) {
            // dT-20;
            String string = "dT-";

            if(num < 10) string = string + "0" + num;
            else string = string + num;
            string = string + ";";

            return string;
        }
    }

    static class GetData {
        static int statusDevice(String string) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // sD-1;
            return Integer.parseInt(sb.substring(3,4));
        }

        static String currentTime(String s) {
            String string;
            StringBuilder sb = new StringBuilder();
            sb.append(s);
            // cT-60-60-24-31-12-99-7;
            string = sb.substring(9,11) + ":";
            string = string +  sb.substring(6,8) + ":";
            string = string +  sb.substring(3,5) + "  ";
            string = string +  sb.substring(12,14) + ".";
            string = string +  sb.substring(15,17) + ".";
            string = string +  sb.substring(18,20) + "  ";
            switch (sb.substring(21, 22)) {
                case "0":
                    string = string + "ВС";
                    break;
                case "1":
                    string = string + "ПН";
                    break;
                case "2":
                    string = string + "ВТ";
                    break;
                case "3":
                    string = string + "СР";
                    break;
                case "4":
                    string = string + "ЧТ";
                    break;
                case "5":
                    string = string + "ПТ";
                    break;
                case "6":
                    string = string + "СБ";
                    break;
            }
            return string;
        }

        static int amountTimers(String string) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // aTs-20;
            return Integer.parseInt(sb.substring(4,6));
        }

        static Timer timer(String string) {
            int num, hours, minutes, weekdays;
            boolean isOn, isEnable;
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            // t-20-24-60-127-1-1;
            num = Integer.parseInt(sb.substring(2,4));
            hours = Integer.parseInt(sb.substring(5,7));
            minutes = Integer.parseInt(sb.substring(8,10));
            weekdays = Integer.parseInt(sb.substring(11,14));
            isOn = sb.substring(15,16).equals("1");
            isEnable = sb.substring(17,18).equals("1");
            return new Timer(num, weekdays, hours, minutes, isOn, isEnable);
        }
    }
}