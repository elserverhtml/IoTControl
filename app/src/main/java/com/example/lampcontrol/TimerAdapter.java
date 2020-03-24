package com.example.lampcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Timer> timers;
    private GetContextFunction getContextFunction;

    TimerAdapter(Context context, List<Timer> timers, GetContextFunction getContextFunction) {
        this.inflater = LayoutInflater.from(context);
        this.timers = timers;
        this.getContextFunction = getContextFunction;
    }

    @NonNull
    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_element_timer, parent, false);
        return new TimerAdapter.ViewHolder(view, getContextFunction);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerAdapter.ViewHolder holder, int position) {
        Timer timer = timers.get(position);
        holder.timerTime.setText(String.format(Locale.getDefault(),"%1$d:%2$d", timer.getH(), timer.getM()));

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
        holder.timerController.setChecked(timer.isEnable());
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView fromStatus, toStatus;
        final TextView timerTime, timerMode;
        final SwitchCompat timerController;
        //final LinearLayout listDevice;

        GetContextFunction getContextFunction;
        ViewHolder(@NonNull View view, GetContextFunction getContextFunction){
            super(view);
            timerTime = view.findViewById(R.id.timerTime);
            timerMode = view.findViewById(R.id.timerMode);
            fromStatus = view.findViewById(R.id.fromStatus);
            toStatus = view.findViewById(R.id.toStatus);
            timerController = view.findViewById(R.id.timerController);
            this.getContextFunction = getContextFunction;

            //listDevice.setOnClickListener(this);
            //deviceStatus.setOnClickListener(this);
            timerController.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.list_device:
//                        deviceClick(v, position);
                        break;
                    case R.id.timerController:
                        switchClick(position);
                        break;
                }
            }
        }

        private void switchClick(int position) {
            Timer selectedTimer = DeviceMenuActivity.timers.get(position);
            selectedTimer.setEnable(timerController.isChecked());
            getContextFunction.update();
        }
    }
}
