package com.example.lampcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapterR extends RecyclerView.Adapter<DeviceAdapterR.ViewHolder> {
    private LayoutInflater inflater;
    private List<Device> devices;

    public DeviceAdapterR(Context context, List<Device> devices) {
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageConnect, deviceStatus;
        final TextView nameView, textConnect;
        ViewHolder(@NonNull View view){
            super(view);
            nameView = view.findViewById(R.id.nameView);
            textConnect = view.findViewById(R.id.textConnect);
            imageConnect = view.findViewById(R.id.imageConnect);
            deviceStatus = view.findViewById(R.id.deviceStatus);
        }
    }
}
