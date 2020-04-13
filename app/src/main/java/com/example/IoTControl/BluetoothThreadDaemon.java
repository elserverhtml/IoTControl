package com.example.IoTControl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class BluetoothThreadDaemon extends Thread {
    private boolean isSleeping = false;

    final static private String idForUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static BluetoothAdapter bluetoothAdapter = null;

    private static final ArrayList<DataForDaemon.DeviceInfo> daemonDevices = new ArrayList<>();
    private static ArrayList<ThreadConnect> connects = new ArrayList<>();

    private static boolean isActivityDeviceWaitToStart = false;
    private boolean isNeedRestartActivityConnection = false;
    private DataForDaemon.DeviceInfo activityDevice;
    private ThreadConnect activityThreadConnect;

    BluetoothThreadDaemon(final Context context) {
        Runnable runnable = null;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
                }
            };
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Bluetooth не поддерживается вашим утройством(", Toast.LENGTH_LONG).show();
                    }
                };
            }
        }
        if(runnable != null) ((MainActivity) context).runOnUiThread(runnable);
        setDaemon(true);
    }

    List<String> findDevices() {
        List<String> list = new ArrayList<>();
        if(bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                for (BluetoothDevice device : devices) {
                    list.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
        return list;
    }

    void checkDevice(int position) {
        synchronized (daemonDevices) {
            for (DataForDaemon.DeviceInfo selectedDevice : daemonDevices) {
                if (selectedDevice.position == position) {
                    activityDevice = selectedDevice;
                    isActivityDeviceWaitToStart = true;
                    connects.get(daemonDevices.indexOf(selectedDevice)).close();
                    daemonDevices.remove(selectedDevice);
                    MainActivity.devices.get(position).checkingDevice(false);
                }
            }
        }
    }

    void setActivityDevice (DataForDaemon.DeviceInfo device) {
        if(activityDevice == null) {
            activityDevice = device;
            activityStarted();
        }
    }

    private void activityStarted() {
        final DataForDaemon.DeviceInfo deviceInfo = activityDevice;
        isActivityDeviceWaitToStart = false;
        isNeedRestartActivityConnection = false;
        if(!bluetoothAdapter.isEnabled()) {
            ((DeviceMenuActivity) deviceInfo.parentContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(deviceInfo.parentContext, "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    ((DeviceMenuActivity) deviceInfo.parentContext).findViewById(R.id.infoTextView).setVisibility(View.VISIBLE);
                    ((DeviceMenuActivity) deviceInfo.parentContext).findViewById(R.id.loadTimers).setVisibility(View.GONE);
                }
            });
            isNeedRestartActivityConnection = true;
            return;
        }
        Log.d("DaemonActivity", "Работа с устройством");
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MainActivity.devices.get(deviceInfo.position).getBT_MAC());
        UUID uuid = UUID.fromString(idForUUID);
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            activityThreadConnect = new ThreadConnect(socket, deviceInfo.parentContext, deviceInfo.position);
            activityThreadConnect.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void activityClosed() {
        MainActivity.data.clearCommands();
        activityDevice = null;
        isNeedRestartActivityConnection = false;
        if(activityThreadConnect != null) {
            activityThreadConnect.interrupt();
            activityThreadConnect = null;
        }
    }

    boolean isSleep() {
        return isSleeping;
    }

    @Override
    public void run() {
        DataForDaemon.DeviceInfo currentDevice;
        BluetoothDevice device;
        UUID uuid;
        BluetoothSocket socket;
        ThreadConnect threadConnect;
        while(!isInterrupted()) {
            if(!bluetoothAdapter.isEnabled()) {
                if(activityDevice != null) {
                    DeviceMenuActivity.isBluetoothOn = false;

                }
                isSleeping = true;
                while (!bluetoothAdapter.isEnabled()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        interrupt();
                        break;
                    }
                }
                isSleeping = false;
                if(isNeedRestartActivityConnection) activityStarted();
                continue;
            }
            if(connects.size() >= 3) continue;

            currentDevice = MainActivity.data.getDeviceFromQueue();
            if(currentDevice == null) continue;
            Log.d("Daemon", "Устройство получено из очереди");

            synchronized (daemonDevices) {
                daemonDevices.add(currentDevice);
            }

            Log.d("Daemon", "Работа с устройством");
            device = bluetoothAdapter.getRemoteDevice(MainActivity.devices.get(currentDevice.position).getBT_MAC());
            uuid = UUID.fromString(idForUUID);
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                threadConnect = new ThreadConnect(socket, currentDevice.parentContext, currentDevice.position, currentDevice.isSet, currentDevice.isOnLamp);
                connects.add(threadConnect);
                Log.d("Daemon", "Запущен поток подключения");
                threadConnect.start();
            } catch (IOException e) {
                MainActivity.data.addDevice(currentDevice);
                e.printStackTrace();
            }
        }
    }

    private static class ThreadConnect extends Thread {
        private Context context;
        private int position;
        private boolean isActivityDevice;
        private BluetoothSocket bluetoothSocket;
        private boolean isSet;
        private boolean isOnLamp;

        private InputStream connectedInputStream = null;
        private OutputStream connectedOutputStream = null;
        private String print;
        private StringBuilder sb = new StringBuilder();

        private ThreadConnect(BluetoothSocket socket, Context context, int position, boolean isSet, boolean isOnLamp) {
            this.bluetoothSocket = socket;
            this.context = context;
            this.position = position;
            this.isSet = isSet;
            this.isOnLamp = isOnLamp;
            this.isActivityDevice = false;
        }

        private ThreadConnect(BluetoothSocket socket, Context context, int position) {
            this.bluetoothSocket = socket;
            this.context = context;
            this.position = position;
            this.isActivityDevice = true;
        }

        @Override
        public void run() {
            boolean connectSuccess = false;
            try {
                bluetoothSocket.connect();
                connectSuccess = true;
            }catch (IOException e) {
                e.printStackTrace();

                MainActivity.devices.get(position).setConnectionStatus(Device.DEVICE_STATUS_OFFLINE);
                MainActivity.devices.get(position).checkingDevice(false);

                if(isActivityDevice) {
                    ((DeviceMenuActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((DeviceMenuActivity) context).findViewById(R.id.loadTimers).setVisibility(View.GONE);
                            ((DeviceMenuActivity) context).findViewById(R.id.infoTextView).setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.adapterR.notifyItemChanged(position);
                        }
                    });
                }

                close();
            }

            if(connectSuccess) {
                Log.d("Daemon", "Устройство подключено");

                MainActivity.devices.get(position).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
                MainActivity.devices.get(position).setDeviceIsOn(Device.DEVICE_ON_OR_OFF);

                if(!isActivityDevice) {
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.adapterR.notifyItemChanged(position);
                        }
                    });
                }

                if(isInterrupted()) {
                    close();
                    return;
                }

                try {
                    connectedInputStream = bluetoothSocket.getInputStream();
                    connectedOutputStream = bluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                if(isActivityDevice) startActivityCommunication();
                else startCommunication();
            }
        }

        void startCommunication(){
            Log.d("Daemon", "Отправка данных");
            try {
                if(isSet) {
                    if(isOnLamp) connectedOutputStream.write("sSD-1;".getBytes());
                    else connectedOutputStream.write("sSD-0;".getBytes());
                }
                connectedOutputStream.write("gSD;".getBytes());
                Log.d("Daemon", "Данные отправлены");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Daemon", "Неудалось отправить данные");
                return;
            }

            Log.d("Daemon", "Получение данных");
            int counter = 0;
            while (counter < 50) {
                counter++;
                try {
                    if(connectedInputStream.available() <= 0) {
                        Thread.sleep(100);
                        Log.d("Daemon", "Данных нет");
                        if(counter == 50) {
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.devices.get(position).setDeviceIsOn(Device.DEVICE_ON_OR_OFF);
                                    MainActivity.devices.get(position).checkingDevice(false);
                                    MainActivity.adapterR.notifyItemChanged(position);
                                }
                            });
                        }
                        continue;
                    }

                    Log.d("Daemon", "Данные получаются");
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf(";");

                    if(endOfLineIndex > 0) {
                        Log.d("Daemon", "Данные получены");
                        print = sb.substring(0, endOfLineIndex);
                        if(print.startsWith("sD")) {
                            Log.d("Daemon", "Ответ правильный - " + print);
                            sb.delete(0, 3);
                            print = sb.substring(0, sb.indexOf(";"));
                            Log.d("Daemon", "Ответ на проверку - " + print);
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(print.equals("1")) {
                                        MainActivity.devices.get(position).setDeviceIsOn(Device.DEVICE_ON);
                                    } else {
                                        MainActivity.devices.get(position).setDeviceIsOn(Device.DEVICE_OFF);
                                    }
                                    MainActivity.devices.get(position).checkingDevice(false);
                                    MainActivity.adapterR.notifyItemChanged(position);
                                }
                            });
                            break;
                        }
                        sb.delete(0, sb.length());
                    }
                } catch (IOException | InterruptedException e) {
                    counter = 50;
                }
            }
            Log.d("Daemon", "Закрытие потока");
            close();
        }

        void startActivityCommunication(){
            while(!isInterrupted()) {
                String sendData;
                if ((sendData = MainActivity.data.getCommandForActivityDevice()) != null) {
                    try {
                        Log.d("DaemonActivity", "Отправка данных " + sendData);
                        connectedOutputStream.write(sendData.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    if(connectedInputStream.available() <= 0) {
                        Thread.sleep(100);
                        continue;
                    }

                    Log.d("DaemonActivity", "Данные получаются");
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf(";");

                    if(endOfLineIndex > 0) {
                        Log.d("DaemonActivity", "Данные получены");
                        print = sb.substring(0, endOfLineIndex);
                        print = print.replace("\n", "");
                        print = print.replace("\r", "");
                        Log.d("DaemonActivity", print);

                        ((DeviceMenuActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(print.startsWith("sD")) {
                                    Log.d("DaemonActivity", "Совпадение с sD");
                                    int isOn = BTDevice.GetData.statusDevice(print);
                                    MainActivity.devices.get(position).setDeviceIsOn(isOn);
                                    ((Switch) ( (DeviceMenuActivity) context).findViewById(R.id.statusDeviceSwitch)).setChecked(isOn == 1);
                                } else if(print.startsWith("cT")) {
                                    Log.d("DaemonActivity", "Совпадение с cT");
                                    ((TextView) ( (DeviceMenuActivity) context).findViewById(R.id.deviceTime)).setText(BTDevice.GetData.currentTime(print));
                                    ((DeviceMenuActivity) context).timeGotten();
                                } else if(print.startsWith("aTs")) {
                                    Log.d("DaemonActivity", "Совпадение с aTs");
                                    DeviceMenuActivity.amountTimers = BTDevice.GetData.amountTimers(print);
                                    ((DeviceMenuActivity) context).putTimers();
                                } else if (print.startsWith("t")) {
                                    Log.d("DaemonActivity", "Совпадение с t");
                                    Timer timer = BTDevice.GetData.timer(print);
                                    if(timer.getId() < DeviceMenuActivity.timers.size()) {
                                        DeviceMenuActivity.timers.set(timer.getId(), timer);
                                        DeviceMenuActivity.adapterR.notifyItemChanged(timer.getId());
                                    } else {
                                        DeviceMenuActivity.timers.add(timer);
                                        DeviceMenuActivity.adapterR.notifyItemInserted(timer.getId());
                                    }
                                    ((DeviceMenuActivity) context).timerGotten();
                                }
                            }
                        });
                        sb.delete(0, sb.length());
                    }
                } catch (IOException | InterruptedException e) {
                    interrupt();
                }
            }
            Log.d("DaemonActivity", "Закрытие потока");
            close();
        }

        void close() {
            synchronized (daemonDevices) {
                try {
                    daemonDevices.remove(connects.indexOf(this));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
            connects.remove(this);
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isActivityDeviceWaitToStart) MainActivity.daemon.activityStarted();
        }
    }
}
