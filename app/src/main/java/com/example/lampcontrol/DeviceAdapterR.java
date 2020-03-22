package com.example.lampcontrol;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapterR extends RecyclerView.Adapter<DeviceAdapterR.ViewHolder> {
    private LayoutInflater inflater;
    private List<Device> devices;

    DeviceAdapterR(Context context, List<Device> devices) {
        this.inflater = LayoutInflater.from(context);
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_element_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.nameView.setText(device.getName());
        if(device.isConnected()) {
            holder.textConnect.setText(R.string.online);
            holder.imageConnect.setImageResource(device.getImageConnected());
        } else {
            holder.textConnect.setText(R.string.offline);
            holder.imageConnect.setImageResource(device.getImageDisconnected());
        }
        if(device.isOn()) {
            holder.deviceStatus.setImageResource(device.getImageDeviceOn());
        } else holder.deviceStatus.setImageResource(device.getImageDeviceOff());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageConnect, deviceStatus;
        final TextView nameView, textConnect;
        final LinearLayout listDevice;
        ViewHolder(@NonNull View view){
            super(view);
            listDevice = view.findViewById(R.id.list_device);
            nameView = view.findViewById(R.id.nameView);
            textConnect = view.findViewById(R.id.textConnect);
            imageConnect = view.findViewById(R.id.imageConnect);
            deviceStatus = view.findViewById(R.id.deviceStatus);

            listDevice.setOnClickListener(this);
            deviceStatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.list_device:
                        deviceClick(v, position);
                        break;
                    case R.id.deviceStatus:
                        statusClick(v, position);
                        break;
                }
            }
        }

        private void deviceClick(View v, int position) {
            Device selectedDevice = ((MainActivity) v.getContext()).getDevices().get(position);
            Toast.makeText(v.getContext(), "Был выбран пункт " + selectedDevice.getName(),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(v.getContext(), DeviceMenuActivity.class);
            v.getContext().startActivity(intent);
        }

        private void statusClick(View v, int position) {
            Device selectedDevice = ((MainActivity) v.getContext()).getDevices().get(position);
            selectedDevice.setDeviceIsOn(!selectedDevice.isOn());
            if(selectedDevice.isOn()) {
                deviceStatus.setImageResource(selectedDevice.getImageDeviceOn());
            } else deviceStatus.setImageResource(selectedDevice.getImageDeviceOff());
        }
    }
}
