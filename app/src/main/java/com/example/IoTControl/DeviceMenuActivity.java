package com.example.IoTControl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DeviceMenuActivity extends AppCompatActivity {
    private boolean isStart = true;
    private boolean isEditMode;

    public static int amountTimers;
    public static List<Timer> timers;
    static TimerAdapter adapterR;
    static boolean isBluetoothOn = true;

    Switch statusDevice;
    ImageButton syncButton, addButton, editButton, OKButton;
    LinearLayout buttonBar;

    static int devicePosition;
    GregorianCalendar gregorianCalendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_menu);

        timers = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.timerListR);
        adapterR = new TimerAdapter(this, timers);
        recyclerView.setAdapter(adapterR);

        statusDevice = findViewById(R.id.statusDeviceSwitch);
        syncButton = findViewById(R.id.buttonSync);
        addButton = findViewById(R.id.addButtonTimer);
        editButton = findViewById(R.id.editButtonTimers);
        OKButton = findViewById(R.id.OKButtonTimers);
        buttonBar = findViewById(R.id.buttonBar);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            devicePosition = arguments.getInt(Device.class.getSimpleName());
            MainActivity.devices.get(devicePosition).connectToDevice(this);

            ((TextView) findViewById(R.id.deviceName)).setText(MainActivity.devices.get(devicePosition).getName());
            workWithActivity();
        }

        MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.getStatusDevice());
        MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.getCurrentTime());
        MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.getAmountTimers());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.daemon.activityClosed();
        timers = null;
    }

    void workWithActivity() {
        statusDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.setStatusDevice(isChecked));
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                gregorianCalendar.getTime();
                MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.setCurrentTime(
                        gregorianCalendar.get(Calendar.SECOND), gregorianCalendar.get(Calendar.MINUTE),
                        gregorianCalendar.get(Calendar.HOUR_OF_DAY), gregorianCalendar.get(Calendar.DAY_OF_MONTH),
                        gregorianCalendar.get(Calendar.MONTH) + 1, gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1));
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                addTimer();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                editModeTimers(true);
            }
        });
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModeTimers(false);
            }
        });
    }

    void addTimer() {
        Intent intent = new Intent(getApplicationContext(), AddOrEditTimerActivity.class);
        intent.putExtra(Timer.class.getSimpleName(), -1);
        startActivity(intent);
    }

    void editModeTimers(boolean isEdit) {
        if(isEdit) {
            isEditMode = true;
            adapterR.notifyItemRangeChanged(0, timers.size());
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            OKButton.setVisibility(View.VISIBLE);
        } else {
            isEditMode = false;
            addButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            OKButton.setVisibility(View.GONE);
            adapterR.notifyItemRangeChanged(0, timers.size());
        }
    }

    boolean isEditMode() {
        return isEditMode;
    }

    void timeGotten() {
        if(isStart) {
            Log.d("DeviceMenuActivity", "Показывается время");
            findViewById(R.id.infoView).setVisibility(View.VISIBLE);
        }
    }

    void putTimers() {
        Log.d("DeviceMenuActivity", "aTs " + amountTimers);
        if(amountTimers == 0) {
            ((TextView) findViewById(R.id.infoTextView)).setText("Устройство не имеет таймеров. Добавьте новый.");
            findViewById(R.id.infoTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.loadTimers).setVisibility(View.GONE);
            findViewById(R.id.statusDeviceSwitch).setEnabled(true);
            findViewById(R.id.buttonSync).setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < amountTimers; i++) {
                MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.getTimer(i));
            }
            if(!isEditMode) editButton.setVisibility(View.VISIBLE);
        }
        if(isStart) buttonBar.setVisibility(View.VISIBLE);
    }

    void timerGotten() {
        if(isStart) {
            if(timers.size() < amountTimers) return;
            isStart = false;
            findViewById(R.id.timerListR).setVisibility(View.VISIBLE);
            findViewById(R.id.loadTimers).setVisibility(View.GONE);
            findViewById(R.id.statusDeviceSwitch).setEnabled(true);
            findViewById(R.id.buttonSync).setVisibility(View.VISIBLE);
        }
    }
}
