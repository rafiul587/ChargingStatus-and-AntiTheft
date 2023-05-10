package com.chargingstatusmonitor.souhadev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.chargingstatusmonitor.BatteryListener;

public class BatteryInfoReceiver extends BroadcastReceiver {

    private BatteryListener batteryListener;

    public void setBatteryListener(BatteryListener listener) {
        this.batteryListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Call the callback method with the batteryPct
        if (batteryListener != null) {
            batteryListener.onBatteryChanged(intent);
        }
    }

    /*if (!Settings.canDrawOverlays(this)) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }*/

}
