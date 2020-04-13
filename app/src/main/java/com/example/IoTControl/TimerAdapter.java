package com.example.IoTControl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Timer> timers;

    TimerAdapter(Context context, List<Timer> timers) {
        this.inflater = LayoutInflater.from(context);
        this.timers = timers;
    }

    @NonNull
    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_element_timer, parent, false);
        return new TimerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerAdapter.ViewHolder holder, int position) {
        Timer timer = timers.get(position);
        String s = timer.getH() + ":";
        if(timer.getM() < 10) s = s + "0" + timer.getM();
        else s = s + timer.getM();
        holder.timerTime.setText(s);

        int modeRes = Timer.getConstStringDays(timer.getDays());
        if(modeRes != -1) holder.timerMode.setText(modeRes);
        else holder.timerMode.setText(Timer.getStringDays(timer.getDays()));

        if(timer.isOn()) {
            holder.fromStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
            holder.toStatus.setImageResource(Device.IMAGE_DEVICE_ON);
        } else {
            holder.fromStatus.setImageResource(Device.IMAGE_DEVICE_ON);
            holder.toStatus.setImageResource(Device.IMAGE_DEVICE_OFF);
        }

        if(((DeviceMenuActivity) holder.timerTime.getContext()).isEditMode()) {
            holder.editTimer.setVisibility(View.VISIBLE);
            holder.deleteTimer.setVisibility(View.VISIBLE);
            holder.timerController.setVisibility(View.GONE);
        } else {
            holder.editTimer.setVisibility(View.GONE);
            holder.deleteTimer.setVisibility(View.GONE);
            holder.timerController.setVisibility(View.VISIBLE);
            holder.timerController.setChecked(timer.isEnable());
        }
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView fromStatus, toStatus, editTimer, deleteTimer;
        final TextView timerTime, timerMode;
        final SwitchCompat timerController;

        ViewHolder(@NonNull View view){
            super(view);
            timerTime = view.findViewById(R.id.timerTime);
            timerMode = view.findViewById(R.id.timerMode);
            fromStatus = view.findViewById(R.id.fromStatus);
            toStatus = view.findViewById(R.id.toStatus);
            editTimer = view.findViewById(R.id.editTimer);
            deleteTimer = view.findViewById(R.id.deleteTimer);
            timerController = view.findViewById(R.id.timerController);

            editTimer.setOnClickListener(this);
            deleteTimer.setOnClickListener(this);
            timerController.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.editTimer:
                        editClick(v, position);
                        break;
                    case R.id.deleteTimer:
                        deleteClick(v, position);
                        break;
                    case R.id.timerController:
                        switchClick(v, position);
                        break;
                }
            }
        }

        private void editClick(View v, int pos) {
            if(MainActivity.daemon.isSleep()) {
                Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(v.getContext(), AddOrEditTimerActivity.class);
            intent.putExtra(Timer.class.getSimpleName(), pos);
            v.getContext().startActivity(intent);
        }

        private void deleteClick(View v, int pos) {
            if(MainActivity.daemon.isSleep()) {
                Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                return;
            }
            MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.deleteTimer(pos));
            DeviceMenuActivity.timers.remove(pos);
            DeviceMenuActivity.adapterR.notifyItemRemoved(pos);
            if(DeviceMenuActivity.timers.isEmpty()) {
                ((DeviceMenuActivity) v.getContext()).editModeTimers(false);
                ((TextView) ((DeviceMenuActivity) v.getContext()).findViewById(R.id.infoTextView)).setText("Устройство не имеет таймеров. Добавьте новый.");
                ((DeviceMenuActivity) v.getContext()).findViewById(R.id.infoTextView).setVisibility(View.VISIBLE);
            }
        }

        private void switchClick(View v, int position) {
            if(MainActivity.daemon.isSleep()) {
                Toast.makeText(v.getContext(), "Пожалуйста включите Bluetooth", Toast.LENGTH_LONG).show();
                return;
            }
            MainActivity.data.setCommandForActivityDevice(BTDevice.SendCommands.manageTimer(position, timerController.isChecked()));
        }
    }
}
