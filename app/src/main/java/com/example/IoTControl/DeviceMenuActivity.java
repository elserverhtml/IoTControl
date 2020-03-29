package com.example.IoTControl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceMenuActivity extends AppCompatActivity {
    public static List<Timer> timers = new ArrayList<>();
    TimerAdapter adapterR;
    Switch statusDevice;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_menu);

        statusDevice = findViewById(R.id.statusDeviceSwitch);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            position = arguments.getInt(Device.class.getSimpleName());
            MainActivity.devices.get(position).connectToDevice(this, position);
            workWithActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.daemon.activityClosed();
    }

    void workWithActivity() {
        ((TextView) findViewById(R.id.deviceName)).setText(MainActivity.devices.get(position).getName());
        statusDevice.setChecked(MainActivity.devices.get(position).isOn());
        statusDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.devices.get(position).setDeviceIsOn(isChecked);
            }
        });

        if(MainActivity.devices.get(position).getConnectionStatus() == Device.DEVICE_STATUS_ONLINE) {
            timers.addAll(MainActivity.devices.get(position).getAllTimers());
            RecyclerView recyclerView = findViewById(R.id.timerListR);
            adapterR = new TimerAdapter(this, MainActivity.devices.get(position).getAllTimers());
            recyclerView.setAdapter(adapterR);

            findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
            findViewById(R.id.outOfSightView).setVisibility(View.GONE);
            findViewById(R.id.statusDeviceSwitch).setEnabled(true);
        }
    }
}
