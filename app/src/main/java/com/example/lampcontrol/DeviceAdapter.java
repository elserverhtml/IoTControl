package com.example.lampcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private int layout;
    private List<Device> devices;

    DeviceAdapter(@NonNull Context context, int resource, @NonNull List<Device> objects) {
        super(context, resource, objects);
        this.devices = objects;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device device = devices.get(position);
        viewHolder.nameView.setText(device.getName());
        if(device.isConnected()) {
            viewHolder.textConnect.setText(R.string.online);
            viewHolder.imageConnect.setImageResource(device.getImageConnected());
        } else {
            viewHolder.textConnect.setText(R.string.offline);
            viewHolder.imageConnect.setImageResource(device.getImageDisconnected());
        }
        if(device.isOn()) {
            viewHolder.deviceStatus.setImageResource(device.getImageDeviceOn());
        } else viewHolder.deviceStatus.setImageResource(device.getImageDeviceOff());


        return convertView;
    }

    private static class ViewHolder {
        final ImageView imageConnect, deviceStatus;
        final TextView nameView, textConnect;
        ViewHolder(View view){
            nameView = view.findViewById(R.id.nameView);
            textConnect = view.findViewById(R.id.textConnect);
            imageConnect = view.findViewById(R.id.imageConnect);
            deviceStatus = view.findViewById(R.id.deviceStatus);
        }
    }
}
