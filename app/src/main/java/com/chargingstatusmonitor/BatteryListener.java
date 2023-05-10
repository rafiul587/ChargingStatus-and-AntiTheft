package com.chargingstatusmonitor;

import android.content.Intent;

public interface BatteryListener {
    void onBatteryChanged(Intent batteryPct);
}
