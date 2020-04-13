package com.example.IoTControl;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BTFragment extends Fragment {
    private String nameDevice = "", BT_MAC = "", typeDevice = "";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final AddDeviceActivity addDeviceActivity = (AddDeviceActivity) getActivity();
        if (addDeviceActivity == null) {
            super.onActivityCreated(savedInstanceState);
            return;
        }

        Button create, cancel;
        create = addDeviceActivity.findViewById(R.id.createButton);
        cancel = addDeviceActivity.findViewById(R.id.cancelButton);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDevice = String.valueOf(((TextView) addDeviceActivity.findViewById(R.id.nameDevice)).getText());

                if(nameDevice.equals("")) {
                    Toast.makeText(addDeviceActivity, "Введите название устройства", Toast.LENGTH_LONG).show();
                    return;
                }
                if(BT_MAC.equals("")) {
                    Toast.makeText(addDeviceActivity, "Выберите устройство", Toast.LENGTH_LONG).show();
                    return;
                }
                Device device = new BTDevice(MainActivity.devices.size(), nameDevice , true, BT_MAC);
                MainActivity.devices.add(device);
                addDeviceActivity.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDeviceActivity.finish();
            }
        });

        Spinner boundDeviceSpinner = addDeviceActivity.findViewById(R.id.boundDevicesSpinner);
        ArrayAdapter boundDeviceAdapter = new ArrayAdapter<>(addDeviceActivity, android.R.layout.simple_spinner_item, MainActivity.daemon.findDevices());
        boundDeviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boundDeviceSpinner.setAdapter(boundDeviceAdapter);

        boundDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BT_MAC = (String)parent.getItemAtPosition(position);
                BT_MAC = BT_MAC.substring(BT_MAC.length() - 17);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner typeDeviceSpinner = addDeviceActivity.findViewById(R.id.typeDeviceSpinner);
        ArrayAdapter typeDeviceAdapter = new ArrayAdapter<>(addDeviceActivity, android.R.layout.simple_spinner_item, new String[] {"Лампа", "Розетка"});
        typeDeviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDeviceSpinner.setAdapter(typeDeviceAdapter);

        typeDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeDevice = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }
}
