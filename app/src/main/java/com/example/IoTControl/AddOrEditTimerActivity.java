package com.example.IoTControl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class AddOrEditTimerActivity extends AppCompatActivity {
    ToggleButton monButton, tueButton, wedButton, thuButton, friButton, satButton, sunButton, manageModeButton;
    Button cancelButton, readyButton;
    TimePicker timePicker;
    TextView activityName, modeEditingText;
    ImageView fromStatus, toStatus;

    int position;
    Timer currentTimer;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_timer);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        activityName = findViewById(R.id.activityName);
        modeEditingText = findViewById(R.id.modeEditingText);
        monButton = findViewById(R.id.monButton);
        tueButton = findViewById(R.id.tueButton);
        wedButton = findViewById(R.id.wedButton);
        thuButton = findViewById(R.id.thuButton);
        friButton = findViewById(R.id.friButton);
        satButton = findViewById(R.id.satButton);
        sunButton = findViewById(R.id.sunButton);

        manageModeButton = findViewById(R.id.manageModeButton);
        fromStatus = findViewById(R.id.fromStatus);
        toStatus = findViewById(R.id.toStatus);
        cancelButton = findViewById(R.id.cancelDialog);
        readyButton = findViewById(R.id.readyDialog);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            position = arguments.getInt(Timer.class.getSimpleName());
            if(position != -1) {
                isEdit = true;
                currentTimer = DeviceMenuActivity.timers.get(position);
                activityName.setText("Редактирование таймера");
            } else {
                currentTimer = new Timer(DeviceMenuActivity.timers.size(), 0, 0,0, true, true);
                activityName.setText("Создание нового таймера");
            }
            workWithActivity();
        }
    }

    void workWithActivity() {
        if(isEdit) {
            timePicker.setHour(currentTimer.getH());
            timePicker.setMinute(currentTimer.getM());
        }

        switch (currentTimer.getDays()) {
            case Timer.MODE_EVERYDAY:
                modeEditingText.setText(R.string.timer_everyday);
                break;
            case Timer.MODE_WEEKDAY:
                modeEditingText.setText(R.string.timer_weekday);
                break;
            case Timer.MODE_WEEKEND:
                modeEditingText.setText(R.string.timer_weekend);
                break;
            case Timer.MODE_SINGLY:
                modeEditingText.setText(R.string.timer_singly);
                break;
            default:
                modeEditingText.setText("Выбранные дни");
        }

        if( (currentTimer.getDays() >> 5 & 1) == 1) monButton.setChecked(true);
        if( (currentTimer.getDays() >> 4 & 1) == 1) tueButton.setChecked(true);
        if( (currentTimer.getDays() >> 3 & 1) == 1) wedButton.setChecked(true);
        if( (currentTimer.getDays() >> 2 & 1) == 1) thuButton.setChecked(true);
        if( (currentTimer.getDays() >> 1 & 1) == 1) friButton.setChecked(true);
        if( (currentTimer.getDays() & 1) == 1) satButton.setChecked(true);
        if( (currentTimer.getDays() >> 6 & 1) == 1) sunButton.setChecked(true);

        manageModeButton.setChecked(currentTimer.isOn());
        if(currentTimer.isOn()) {
            fromStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
            toStatus.setImageResource(Device.IMAGE_DEVICE_ON);
        } else {
            fromStatus.setImageResource(Device.IMAGE_DEVICE_ON);
            toStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
        }

        manageModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manageModeButton.isChecked()) {
                    fromStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
                    toStatus.setImageResource(Device.IMAGE_DEVICE_ON);
                } else {
                    fromStatus.setImageResource(Device.IMAGE_DEVICE_ON);
                    toStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int days = 0;
                if(monButton.isChecked()) days = days | (1 << 5);
                if(tueButton.isChecked()) days = days | (1 << 4);
                if(wedButton.isChecked()) days = days | (1 << 3);
                if(thuButton.isChecked()) days = days | (1 << 2);
                if(friButton.isChecked()) days = days | (1 << 1);
                if(satButton.isChecked()) days = days | 1;
                if(sunButton.isChecked()) days = days | (1 << 6);
                String s;
                if(!isEdit) s = BTDevice.SendCommands.addTimer(timePicker.getHour(), timePicker.getMinute(), days, manageModeButton.isChecked(), currentTimer.isEnable());
                else s = BTDevice.SendCommands.editTimer(currentTimer.getId(), timePicker.getHour(), timePicker.getMinute(), days, manageModeButton.isChecked(), currentTimer.isEnable());
                Log.d("AddTimer", "Кнопка готово нажата, собранная строка: " + s);
                MainActivity.data.setCommandForActivityDevice(s);
                finish();
            }
        });
    }
}
