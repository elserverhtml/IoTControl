package com.example.IoTControl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class BluetoothThreadDaemon extends Thread {
    final static private String idForUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static BluetoothAdapter bluetoothAdapter = null; // TODO

    private static ArrayList<DataForDaemon.DeviceInfo> devices = new ArrayList<>();
    private static ArrayList<ThreadConnect> connects = new ArrayList<>();

    private ThreadConnect activityThreadConnect;
    private ActivityThreadConnected activityConnectedThread;

    BluetoothThreadDaemon(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(context, "BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(context, "Bluetooth не поддерживается вашим утройством(", Toast.LENGTH_LONG).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(context, "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
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

    boolean checkDevice(DataForDaemon.DeviceInfo device) {
        for(DataForDaemon.DeviceInfo selectedDevice: devices) {
            if(selectedDevice.position == device.position) {
                devices.remove(selectedDevice);
                return true;
            }
        }
        return false;
    }

    void activityClosed() {
        if(activityThreadConnect != null) {
            activityThreadConnect.interrupt();
            activityThreadConnect = null;
            if(activityConnectedThread != null) {
                activityConnectedThread.interrupt();
                activityConnectedThread = null;
            }
        }
    }

    @Override
    public void run() {
        DataForDaemon.DeviceInfo currentDevice;
        BluetoothDevice device;
        UUID uuid;
        BluetoothSocket socket;
        ThreadConnect threadConnect;
        while(!isInterrupted()) {
            if(!bluetoothAdapter.isEnabled()) continue; // TODO: уведомление
            if(connects.size() >= 3) continue;

            currentDevice = areThereAnyDevicesToConnect();
            if(currentDevice == null) continue;

            Log.d("Daemon", "Работа с устройством");
            device = bluetoothAdapter.getRemoteDevice(MainActivity.devices.get(currentDevice.position).getBT_MAC());
            uuid = UUID.fromString(idForUUID);
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                if(currentDevice.isActivityDevice) {
                    threadConnect = new ThreadConnect(socket, currentDevice.parentContext, currentDevice.position);
                    activityThreadConnect = threadConnect;
                }
                else {
                    threadConnect = new ThreadConnect(socket, currentDevice.parentContext, currentDevice.position, currentDevice.isSet, currentDevice.isOnLamp);
                    connects.add(threadConnect);
                    Log.d("Daemon", "Запущен поток подключения");
                }
                threadConnect.start();
            } catch (IOException e) {
                MainActivity.data.addDevice(currentDevice);
                e.printStackTrace();
            }
        }
    }

    private DataForDaemon.DeviceInfo areThereAnyDevicesToConnect() {
        if (!MainActivity.data.isEmpty()) {
            Log.d("Daemon", "Устройство получено из очереди");
            return MainActivity.data.getDeviceFromQueue();
        }
        return null;
    }

    private class ThreadConnect extends Thread {
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

        private boolean isCanChangeOnActivityThread = true;

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
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.devices.get(position).setConnectionStatus(Device.DEVICE_STATUS_OFFLINE);
                        MainActivity.adapterR.notifyItemChanged(position);
                    }
                };
                if(isActivityDevice) ((DeviceMenuActivity) context).runOnUiThread(runnable);
                else ((MainActivity) context).runOnUiThread(runnable);

                close();
            }

            if(connectSuccess) {
                Log.d("Daemon", "Устройство подключено");
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.devices.get(position).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
                        MainActivity.adapterR.notifyItemChanged(position);
                    }
                };
                if(isActivityDevice) ((DeviceMenuActivity) context).runOnUiThread(runnable);
                else ((MainActivity) context).runOnUiThread(runnable);

                if(isInterrupted()) {

                }

                isCanChangeOnActivityThread = false;
                if(isActivityDevice) {
                    ActivityThreadConnected threadConnected = new ActivityThreadConnected(bluetoothSocket, context, position);
                    threadConnected.start();
                    activityConnectedThread = threadConnected;
                } else {
                    ThreadConnected threadConnected = new ThreadConnected(bluetoothSocket, context, position, isSet, isOnLamp);
                    threadConnected.setItIsMyParent(this);
                    threadConnected.start();
                    Log.d("Daemon", "Запущен поток передачи");
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

                close();
            }
        }

        void startCommunication(){
            Log.d("Daemon", "Отправка данных");
            boolean isWait = false;
            try {
                if(isSet) {
                    if(isOnLamp) connectedOutputStream.write("cL1".getBytes());
                    else connectedOutputStream.write("cL0".getBytes());
                }
                connectedOutputStream.write("gSL;".getBytes());
                isWait = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Daemon", "Получение данных");
            int counter = 0;
            while (isWait && (counter < 50)) {
                counter++;
                try {
                    if(connectedInputStream.available() <= 0) {
                        Thread.sleep(100);
                        continue;
                    }

                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf("\r\n");

                    if(endOfLineIndex > 0) {
                        print = sb.substring(0, endOfLineIndex);
                        if(print.startsWith("sL")) {
                            sb.delete(0, 2);
                            print = sb.substring(0, sb.indexOf("\r\n"));
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(print.equals("1;")) {
                                        MainActivity.devices.get(position).setDeviceIsOn(true);
                                    } else {
                                        MainActivity.devices.get(position).setDeviceIsOn(false);
                                    }
                                    MainActivity.adapterR.notifyItemChanged(position);
                                }
                            });
                            break;
                        }
                        sb.delete(0, sb.length());
                        // TODO log
                    }
                } catch (IOException | InterruptedException e) {
                    isWait = false;
                }
            }
        }

        void startActivityCommunication(){
            boolean isWaitAnswer = false;
            int timeCounter = 0;

            Log.d("Daemon", "Проверка наличия данных для отправки");
            String sendData;
            if((sendData = MainActivity.data.getCommandForActivityDevice()) != null) {
                try {
                    connectedOutputStream.write(sendData.getBytes());
                    //if(BTDevice.isGetRequest(sendData)) isWaitAnswer = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d("Daemon", "Получение данных");
            while (isWaitAnswer && timeCounter < 50) {
                try {
                    if(connectedInputStream.available() <= 0) {
                        Thread.sleep(100);
                        continue;
                    }

                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf(";");

                    if(endOfLineIndex > 0) {
                        print = sb.substring(0, endOfLineIndex);
                        if(print.startsWith("sL")) {
                            sb.delete(0, 2);
                            print = sb.substring(0, sb.indexOf("\r\n"));
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(print.equals("1;")) {
                                        MainActivity.devices.get(position).setDeviceIsOn(true);
                                    } else {
                                        MainActivity.devices.get(position).setDeviceIsOn(false);
                                    }
                                    MainActivity.adapterR.notifyItemChanged(position);
                                }
                            });
                            break;
                        }
                        sb.delete(0, sb.length());
                        // TODO log
                    }
                } catch (IOException | InterruptedException e) {
                    //TODO
                }
            }
        }

        boolean isCanChangeOnActivityThread() {
            return isCanChangeOnActivityThread;
        }

        void close() {
            connects.remove(this);
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ThreadConnected extends Thread {
        private InputStream connectedInputStream;
        private OutputStream connectedOutputStream;

        private String print;
        private StringBuilder sb = new StringBuilder();
        private Context context;
        private int position;
        private boolean isSet;
        private boolean isOnLamp;

        private ThreadConnect itIsMyParent;

        ThreadConnected(BluetoothSocket socket, Context context, int pos, boolean isSet, boolean isOnLamp) {
            InputStream in = null;
            OutputStream out = null;
            this.context = context;
            this.position = pos;
            this.isSet = isSet;
            this.isOnLamp = isOnLamp;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        void setItIsMyParent(ThreadConnect threadConnect) {
            this.itIsMyParent = threadConnect;
        }

        @Override
        public void run() {
            Log.d("Daemon", "Поток общения начал работу");
            boolean isWait = false;
            int counter = 0;
            try {
                if(isSet) {
                    if(isOnLamp) connectedOutputStream.write("cL1".getBytes());
                    else connectedOutputStream.write("cL0".getBytes());
                }
                connectedOutputStream.write("gSL;".getBytes());
                Log.d("Daemon", "Отправлен запрос на получения статуса лампы");
                isWait = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (isWait && (counter < 50)) {
                if(counter == 0) Log.d("Daemon", "Ожидание ввода");
                Log.d("Daemon", "Counter: " + counter);
                counter++;
                try {
                    Log.d("Daemon", "Вход в try");
                    byte[] buffer = new byte[1];
                    if(connectedInputStream.available() <= 0) {
                        Log.d("Daemon", "Данных нет, сон");
                        Thread.sleep(100);
                        continue;
                    }
                    Log.d("Daemon", "Данные есть, чтение");
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf("\r\n");

                    if(endOfLineIndex > 0) {
                        Log.d("Daemon", "Чтение окончено");
                        print = sb.substring(0, endOfLineIndex);
                        if(print.startsWith("sL")) {
                            Log.d("Daemon", "Ввод получен");
                            sb.delete(0, 2);
                            print = sb.substring(0, sb.indexOf("\r\n"));
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(print.equals("1;")) {
                                        MainActivity.devices.get(position).setDeviceIsOn(true);
                                    } else {
                                        MainActivity.devices.get(position).setDeviceIsOn(false);
                                    }
                                    MainActivity.adapterR.notifyItemChanged(position);
                                }
                            });
                            break;
                        }
                        sb.delete(0, sb.length());
                        // TODO log
                    }
                } catch (IOException | InterruptedException e) {
                    isWait = false;
                }
            }

            Log.d("Daemon", "Окончание ожидания ввода" + counter);
            itIsMyParent.close();
            itIsMyParent = null;
        }
    }

    private static class ActivityThreadConnected extends Thread {
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        private String print;
        private StringBuilder sb = new StringBuilder();
        private Context context;
        private int position;

        ActivityThreadConnected(BluetoothSocket socket, Context context, int pos) {
            InputStream in = null;
            OutputStream out = null;
            this.context = context;
            this.position = pos;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIn = new String(buffer, 0, bytes);
                    sb.append(strIn);
                    int endOfLineIndex = sb.indexOf("\r\n");

                    if(endOfLineIndex > 0) {
                        print = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());

                        ((DeviceMenuActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (print) {
                                    case "gSL":
                                        //TODO
                                        break;
                                    case "gT":
                                        //TODO it
                                        break;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
