package com.example.IoTControl;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BTFragment extends Fragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final AddDeviceActivity addDeviceActivity = (AddDeviceActivity) getActivity();

        Button create;
        if (addDeviceActivity != null) {
            create = addDeviceActivity.findViewById(R.id.createButton);

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device device = new BTDevice(String.valueOf(((TextView) addDeviceActivity.findViewById(R.id.nameDevice)).getText()), true);
                    MainActivity.devices.add(device);
                    addDeviceActivity.finish();
                }
            });
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }
}
