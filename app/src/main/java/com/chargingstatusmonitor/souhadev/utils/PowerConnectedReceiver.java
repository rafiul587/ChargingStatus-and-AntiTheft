package com.chargingstatusmonitor.souhadev.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerConnectedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent batteryStatus) {
        ((MyService) context).onChargerConnected();
    }
}
