package com.example.IoTControl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<Device> devices = new ArrayList<>();
    static DeviceAdapter adapterR;
    static DataForDaemon data = new DataForDaemon();
    static BluetoothThreadDaemon daemon;

    private FloatingActionButton addButton, editButton, wifiSettings, multifunctionButton;

    private Animation animHideAdd, animHideEdit, animHideWFSet, animHideMultifunction,
            animShowAdd, animShowEdit, animShowWFSet, animShowMultifunction;
    private boolean isMenuOpen = false;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareFABs();

        setInitialData();
        devices.get(1).addTimer(new Timer(1, 62, 16, 30, true, true));
        devices.get(1).addTimer(new Timer(2, 5, 16, 30, false, true));
        devices.get(1).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
        devices.get(2).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
        RecyclerView recyclerView = findViewById(R.id.deviceListR);
        adapterR = new DeviceAdapter(this, devices);
        recyclerView.setAdapter(adapterR);

        daemon = new BluetoothThreadDaemon(this);
        daemon.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animHideAdd.setDuration(0);
        animHideEdit.setDuration(0);
        animHideWFSet.setDuration(0);
        addButton.startAnimation(animHideAdd);
        editButton.startAnimation(animHideEdit);
        wifiSettings.startAnimation(animHideWFSet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterR.notifyItemRangeChanged(0, devices.size());
    }

    public void openMenu() {
        if(isMenuOpen) return;
        if(animHideWFSet.getDuration() == 0) {
            animHideAdd.setDuration(200);
            animHideEdit.setDuration(200);
            animHideWFSet.setDuration(200);
        }
        isMenuOpen = true;
        if(!devices.isEmpty()) editButton.startAnimation(animShowEdit);
        addButton.startAnimation(animShowAdd);
        wifiSettings.startAnimation(animShowWFSet);
        multifunctionButton.startAnimation(animShowMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_close);
    }

    public void closeMenu(boolean isFast, boolean isChangeMultiB) {
        if(!isMenuOpen) return;
        isMenuOpen = false;
        if(isFast) {
            animHideAdd.setDuration(30);
            animHideEdit.setDuration(30);
            animHideWFSet.setDuration(30);
        }
        if(!devices.isEmpty()) editButton.startAnimation(animHideEdit);
        addButton.startAnimation(animHideAdd);
        wifiSettings.startAnimation(animHideWFSet);
        if(isChangeMultiB) {
            multifunctionButton.startAnimation(animHideMultifunction);
            multifunctionButton.setImageResource(R.drawable.ic_menu);
        }
    }

    private void editDevices() {
        closeMenu(false, false);
        multifunctionButton.startAnimation(animShowMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_ok);
        isEditMode = true;
        adapterR.notifyItemRangeChanged(0, devices.size());
    }

    void stopEditDevices() {
        multifunctionButton.startAnimation(animHideMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_menu);
        isEditMode = false;
        adapterR.notifyItemRangeChanged(0, devices.size());
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    private void prepareFABs() {
        editButton = findViewById(R.id.editButton);
        addButton = findViewById(R.id.addButton);
        wifiSettings = findViewById(R.id.wifiSettings);
        multifunctionButton = findViewById(R.id.multifunctionButton);

        animHideEdit = AnimationUtils.loadAnimation(this, R.anim.fab_three_hide);
        animShowEdit = AnimationUtils.loadAnimation(this, R.anim.fab_three_show);
        animHideAdd = AnimationUtils.loadAnimation(this, R.anim.fab_two_hide);
        animShowAdd = AnimationUtils.loadAnimation(this, R.anim.fab_two_show);
        animHideWFSet = AnimationUtils.loadAnimation(this, R.anim.fab_one_hide);
        animShowWFSet = AnimationUtils.loadAnimation(this, R.anim.fab_one_show);
        animHideMultifunction = AnimationUtils.loadAnimation(this, R.anim.fab_multifunction_hide);
        animShowMultifunction = AnimationUtils.loadAnimation(this, R.anim.fab_multifunction_show);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen && !devices.isEmpty()) editDevices();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) {
                    closeMenu(true, true);
                    Intent intent = new Intent(getApplicationContext(), AddDeviceActivity.class);
                    startActivity(intent);
                }
            }
        });
        wifiSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        multifunctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) closeMenu(false, true);
                else if(!isEditMode) openMenu();
                else stopEditDevices();
            }
        });
    }

    private void setInitialData(){
        devices.add(new BTDevice(devices.size(), "Комната 1", true, "1D:1D:1D:1D:1D:1D"));
        devices.add(new BTDevice(devices.size(), "Комната 2", true, "2E:2E:2E:2E:2E:2E"));
        devices.add(new BTDevice(devices.size(), "Комната 3", true, "3C:3C:3C:3C:3C:3C"));
        devices.add(new BTDevice(devices.size(), "Комната 4", true, "4A:4A:4A:4A:4A:4A"));
        devices.add(new BTDevice(devices.size(), "Комната 5", true, "5B:5B:5B:5B:5B:5B"));
        devices.add(new BTDevice(devices.size(), "Комната 6", true, "6F:6F:6F:6F:6F:6F"));
        devices.add(new BTDevice(devices.size(), "Комната 7", true, "7E:7E:7E:7E:7E:7E"));
        devices.add(new BTDevice(devices.size(), "Комната 8", true, "8C:8C:8C:8C:8C:8C"));
        devices.add(new BTDevice(devices.size(), "Комната 9", true, "9A:9A:9A:9A:9A:9A"));
        devices.add(new BTDevice(devices.size(), "Комната 10", true, "10:10:10:10:10:10"));
        devices.add(new BTDevice(devices.size(), "Комната 11", true, "11:11:11:11:11:11"));
        devices.add(new BTDevice(devices.size(), "Комната 12", true, "12:12:12:12:12:12"));
        devices.add(new BTDevice(devices.size(), "Комната 13", true, "13:13:13:13:13:13"));
        devices.add(new BTDevice(devices.size(), "Комната 14", true, "14:14:14:14:14:14"));
        devices.add(new BTDevice(devices.size(), "Комната 15", true, "15:15:15:15:15:15"));
        devices.add(new BTDevice(devices.size(), "Комната 16", true, "16:16:16:16:16:16"));
        devices.add(new BTDevice(devices.size(), "Комната 17", true, "17:17:17:17:17:17"));
        devices.add(new BTDevice(devices.size(), "Комната 18", true, "18:18:18:18:18:18"));
        devices.add(new BTDevice(devices.size(), "Комната 19", true, "19:19:19:19:19:19"));
        devices.add(new BTDevice(devices.size(), "Комната 20", true, "20:20:20:20:20:20"));
    }
}
