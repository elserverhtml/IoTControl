package com.example.IoTControl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Device> devices;

    DeviceAdapter(Context context, List<Device> devices) {
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

        if(((MainActivity) holder.deviceStatus.getContext()).isEditMode()) {
            holder.textConnect.setVisibility(View.GONE);
            holder.deviceStatus.setVisibility(View.GONE);
            holder.deviceConnect.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.imageEdit.setVisibility(View.VISIBLE);
            holder.imageDelete.setVisibility(View.VISIBLE);
        } else {
            holder.textConnect.setVisibility(View.VISIBLE);
            holder.imageEdit.setVisibility(View.GONE);
            holder.imageDelete.setVisibility(View.GONE);
            if(!device.isChecking()) {
                holder.progressBar.setVisibility(View.GONE);
                holder.deviceConnect.setVisibility(View.VISIBLE);
                switch (device.getConnectionStatus()) {
                    case Device.DEVICE_STATUS_ONLINE:
                        holder.textConnect.setText(R.string.connection_status_device_online);
                        holder.deviceConnect.setImageResource(device.getImageConnected());
                        holder.deviceStatus.setVisibility(View.VISIBLE);
                        break;
                    case Device.DEVICE_STATUS_OFFLINE:
                        holder.textConnect.setText(R.string.connection_status_device_offline);
                        holder.deviceConnect.setImageResource(device.getImageDisconnected());
                        holder.deviceStatus.setVisibility(View.GONE);
                        break;
                    case Device.DEVICE_STATUS_WAITING:
                        holder.textConnect.setText(R.string.connection_status_device_waiting_for_connection_status_check);
                        holder.deviceConnect.setImageResource(device.getImageConnectUnknown());
                        holder.deviceStatus.setVisibility(View.GONE);
                        break;
                }
            } else {
                holder.deviceStatus.setVisibility(View.GONE);
                holder.deviceConnect.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            if(device.isOn() == Device.DEVICE_ON) {
                holder.deviceStatus.setImageResource(device.getImageDeviceOn());
            } else holder.deviceStatus.setImageResource(device.getImageDeviceOff());
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView deviceConnect, deviceStatus, imageEdit, imageDelete;
        final TextView nameView, textConnect;
        final LinearLayout listDevice;
        final ProgressBar progressBar;

        ViewHolder(@NonNull View view){
            super(view);
            listDevice = view.findViewById(R.id.list_device);
            nameView = view.findViewById(R.id.nameView);
            textConnect = view.findViewById(R.id.textConnect);
            deviceConnect = view.findViewById(R.id.deviceConnect);
            deviceStatus = view.findViewById(R.id.deviceStatus);
            imageEdit = view.findViewById(R.id.imageEdit);
            imageDelete = view.findViewById(R.id.imageDelete);
            progressBar = view.findViewById(R.id.progressBar);

            listDevice.setOnClickListener(this);
            deviceStatus.setOnClickListener(this);
            deviceConnect.setOnClickListener(this);
            imageEdit.setOnClickListener(this);
            imageDelete.setOnClickListener(this);
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
                    case R.id.deviceConnect:
                        connectClick(v, position);
                        break;
                    case R.id.imageEdit:
                        //TODO: editClick();
                        break;
                    case R.id.imageDelete:
                        deleteClick(v, position);
                        break;
                }
            }
        }

        private void deviceClick(View v, int position) {
            if(!((MainActivity) v.getContext()).isEditMode()) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                ((MainActivity) v.getContext()).closeMenu(true, true);
                Intent intent = new Intent(v.getContext(), DeviceMenuActivity.class);
                intent.putExtra(Device.class.getSimpleName(), position);
                v.getContext().startActivity(intent);
            }
        }

        private void statusClick(View v, int position) {
            Device selectedDevice = MainActivity.devices.get(position);
            if(MainActivity.daemon.isSleep()) {
                Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                selectedDevice.setConnectionStatus(Device.DEVICE_STATUS_WAITING);
            } else {
                selectedDevice.controlDeviceFromList(v.getContext(), selectedDevice.isOn() != Device.DEVICE_ON);
                selectedDevice.checkingDevice(true);
            }
            MainActivity.adapterR.notifyItemChanged(position);
        }

        private void connectClick(View v, int position) {
            if(MainActivity.devices.get(position).getConnectionStatus() != Device.DEVICE_STATUS_ONLINE) {
                if(MainActivity.daemon.isSleep()) {
                    Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.devices.get(position).isCanConnect(v.getContext());
                MainActivity.devices.get(position).checkingDevice(true);
                MainActivity.adapterR.notifyItemChanged(position);
            }
        }

        private void deleteClick(View v, int position) {
            MainActivity.devices.remove(position);
            MainActivity.adapterR.notifyItemRemoved(position);
            if(MainActivity.devices.isEmpty()) ((MainActivity) v.getContext()).stopEditDevices();
        }
    }
}
