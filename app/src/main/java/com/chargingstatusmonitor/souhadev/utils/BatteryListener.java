package com.chargingstatusmonitor.souhadev.utils;

import android.content.Intent;

public interface BatteryListener {
    void onBatteryChanged(Intent batteryPct);
}
