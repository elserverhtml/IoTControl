package com.example.IoTControl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<Device> devices = new ArrayList<>();
    static DeviceAdapter adapterR;


    private FloatingActionButton addButton, editButton, syncButton, multifunctionButton;

    private Animation animHideAdd, animHideEdit, animHideSync, animHideMultifunction,
            animShowAdd, animShowEdit, animShowSync, animShowMultifunction;
    private boolean isMenuOpen = false;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareFABs();

        setInitialData();
        devices.get(1).addTimer(new Timer(1, 62, 16, 30, true, true));
        devices.get(1).addTimer(new Timer(2, 5, 16, 30, false, true));
        devices.get(1).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
        devices.get(2).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
        RecyclerView recyclerView = findViewById(R.id.deviceListR);
        adapterR = new DeviceAdapter(this, devices);
        recyclerView.setAdapter(adapterR);
    }

    @Override
    protected void onStart() {
        super.onStart();

        animHideAdd.setDuration(0);
        animHideEdit.setDuration(0);
        animHideSync.setDuration(0);
        addButton.startAnimation(animHideAdd);
        editButton.startAnimation(animHideEdit);
        syncButton.startAnimation(animHideSync);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterR.notifyDataSetChanged();
    }

    private void openMenu() {
        if(animHideSync.getDuration() == 0) {
            animHideAdd.setDuration(200);
            animHideEdit.setDuration(200);
            animHideSync.setDuration(200);
        }
        isMenuOpen = true;
        addButton.startAnimation(animShowAdd);
        editButton.startAnimation(animShowEdit);
        syncButton.startAnimation(animShowSync);
        multifunctionButton.startAnimation(animShowMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_close);
        devices.get(0).setConnectionStatus(Device.DEVICE_STATUS_ONLINE);
        adapterR.notifyItemChanged(0);
    }

    private void closeMenu(boolean isFast, boolean isChangeMultiB) {
        isMenuOpen = false;
        if(isFast) {
            animHideAdd.setDuration(30);
            animHideEdit.setDuration(30);
            animHideSync.setDuration(30);
        }
        addButton.startAnimation(animHideAdd);
        editButton.startAnimation(animHideEdit);
        syncButton.startAnimation(animHideSync);
        if(isChangeMultiB) {
            multifunctionButton.startAnimation(animHideMultifunction);
            multifunctionButton.setImageResource(R.drawable.ic_menu);
        }
        devices.get(0).setConnectionStatus(Device.DEVICE_STATUS_OFFLINE);
        adapterR.notifyItemChanged(0);
    }

    private void editDevices() {
        closeMenu(false, false);
        multifunctionButton.startAnimation(animShowMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_ok);
        isEditMode = true;
        adapterR.notifyItemRangeChanged(0, devices.size());
    }

    private void stopEditDevices() {
        multifunctionButton.startAnimation(animHideMultifunction);
        multifunctionButton.setImageResource(R.drawable.ic_menu);
        isEditMode = false;
        adapterR.notifyItemRangeChanged(0, devices.size());
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    private void prepareFABs() {
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);
        syncButton = findViewById(R.id.syncButton);
        multifunctionButton = findViewById(R.id.multifunctionButton);

        animHideAdd = AnimationUtils.loadAnimation(this, R.anim.fab_add_hide);
        animHideAdd.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                addButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animShowAdd = AnimationUtils.loadAnimation(this, R.anim.fab_add_show);
        animHideEdit = AnimationUtils.loadAnimation(this, R.anim.fab_edit_hide);
        animHideEdit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                editButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animShowEdit = AnimationUtils.loadAnimation(this, R.anim.fab_edit_show);
        animHideSync = AnimationUtils.loadAnimation(this, R.anim.fab_sync_hide);
        animHideSync.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                syncButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animShowSync = AnimationUtils.loadAnimation(this, R.anim.fab_sync_show);
        animHideMultifunction = AnimationUtils.loadAnimation(this, R.anim.fab_multifunction_hide);
        animShowMultifunction = AnimationUtils.loadAnimation(this, R.anim.fab_multifunction_show);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) {
                    closeMenu(true, true);
                    Intent intent = new Intent(getApplicationContext(), AddDeviceActivity.class);
                    startActivity(intent);
                }
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) editDevices();
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) stopEditDevices();
            }
        });
        multifunctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) closeMenu(false, true);
                else if(!isEditMode) openMenu();
                else stopEditDevices();
            }
        });
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
        devices.add(new BTDevice("Комната 12", true));
        devices.add(new BTDevice("Комната 13", true));
        devices.add(new BTDevice("Комната 14", true));
        devices.add(new BTDevice("Комната 15", true));
        devices.add(new BTDevice("Комната 16", true));
        devices.add(new BTDevice("Комната 17", true));
        devices.add(new BTDevice("Комната 18", true));
        devices.add(new BTDevice("Комната 19", true));
        devices.add(new BTDevice("Комната 20", true));
    }
}
