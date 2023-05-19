package com.chargingstatusmonitor.souhadev.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerDisconnectedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent batteryStatus) {
        Log.d("TAG", "onReceive: ");
        ((MyService) context).onChargerDisconnected();
    }
}
