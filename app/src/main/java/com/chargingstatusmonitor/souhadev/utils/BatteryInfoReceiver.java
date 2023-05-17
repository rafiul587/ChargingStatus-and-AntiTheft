package com.chargingstatusmonitor.souhadev.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chargingstatusmonitor.souhadev.utils.BatteryListener;

public class BatteryInfoReceiver extends BroadcastReceiver {

    private BatteryListener batteryListener;

    public void setBatteryListener(BatteryListener listener) {
        this.batteryListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Call the callback method with the intent
        if (batteryListener != null) {
            batteryListener.onBatteryChanged(intent);
        }
    }
}
