package com.example.lampcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Device> devices = new ArrayList<>();
    private FloatingActionButton editButton, addButton, OKButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editButton = findViewById(R.id.editButton);
        addButton = findViewById(R.id.addButton);
        OKButton = findViewById(R.id.OKButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDevices();
            }
        });
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopEditDevices();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDeviceActivity.class);
                startActivity(intent);
            }
        });

        setInitialData();

        RecyclerView recyclerView = findViewById(R.id.deviceListR);
        DeviceAdapterR adapterR = new DeviceAdapterR(this, devices);
        recyclerView.setAdapter(adapterR);
    }

    private void editDevices() {
        editButton.setVisibility(View.GONE);
        addButton.setVisibility(View.VISIBLE);
        OKButton.setVisibility(View.VISIBLE);
    }

    private void stopEditDevices() {
        editButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.GONE);
        OKButton.setVisibility(View.GONE);
    }

    public List<Device> getDevices() {
        return devices;
    }

    private void setInitialData(){
        devices.add(new BTDevice("Комната 1", true));
        devices.add(new BTDevice("Комната 2", true));
        devices.add(new BTDevice("Комната 3", true));
        devices.add(new BTDevice("Комната 4", true));
        devices.add(new BTDevice("Комната 5", true));
        devices.add(new BTDevice("Комната 6", true));
        devices.add(new BTDevice("Комната 7", true));
        devices.add(new BTDevice("Комната 8", true));
        devices.add(new BTDevice("Комната 9", true));
        devices.add(new BTDevice("Комната 10", true));
        devices.add(new BTDevice("Комната 11", true));
    }
}
