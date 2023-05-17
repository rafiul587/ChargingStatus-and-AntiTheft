package com.chargingstatusmonitor.souhadev.ui.batteryinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.utils.BatteryListener;
import com.chargingstatusmonitor.souhadev.utils.BatteryInfoReceiver;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentBatteryInfoBinding;

public class BatteryInfoFragment extends Fragment implements BatteryListener {

    private FragmentBatteryInfoBinding binding;

    BatteryInfoReceiver batteryInfoReceiver;


    public BatteryInfoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBatteryInfoBinding.inflate(inflater, container, false);
        batteryInfoReceiver = new BatteryInfoReceiver();
        batteryInfoReceiver.setBatteryListener(this);
        binding.expandOrCollapse.setOnClickListener(v -> {
            if (binding.moreInfoLayout.getRoot().getVisibility() == View.GONE) {
                binding.moreInfoLayout.getRoot().setVisibility(View.VISIBLE);
                binding.expandOrCollapse.setRotation(90);
            } else {
                binding.moreInfoLayout.getRoot().setVisibility(View.GONE);
                binding.expandOrCollapse.setRotation(-90);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(batteryInfoReceiver);
    }

    @Override
    public void onBatteryChanged(Intent intent) {
        boolean isExtraPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (isExtraPresent) {
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            int healthCondition;

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthCondition = R.string.battery_health_cold;
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthCondition = R.string.battery_health_dead;
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthCondition = R.string.battery_health_good;
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthCondition = R.string.battery_health_over_voltage;
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthCondition = R.string.battery_health_overheat;
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthCondition = R.string.battery_health_unspecified_failure;
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                default:
                    healthCondition = R.string.battery_health_unknown;
                    break;
            }

            binding.health.setText(getString(healthCondition));

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                int batteryPct = (int) ((level / (float) scale) * 100f);
                binding.batteryLevel.setText(batteryPct + " %");
                binding.batteryLevelWave.setProgressValue(batteryPct);
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int chargingType = R.string.battery_plugged_none;

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    chargingType = R.string.battery_plugged_wireless;
                    break;

                case BatteryManager.BATTERY_PLUGGED_USB:
                    chargingType = R.string.battery_plugged_usb;
                    break;

                case BatteryManager.BATTERY_PLUGGED_AC:
                    chargingType = R.string.battery_plugged_ac;
                    break;

                default:
                    break;
            }

            binding.chargingType.setText(getString(chargingType));

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int chargingStatus;

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    chargingStatus = R.string.battery_status_charging;
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    chargingStatus = R.string.battery_status_full;
                    break;

                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    chargingStatus = R.string.battery_status_unknown;
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                default:
                    chargingStatus = R.string.battery_status_discharging;
                    break;
            }

            binding.status.setText(getString(chargingStatus));

            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    binding.moreInfoLayout.technology.setText(technology);
                } else
                    binding.moreInfoLayout.technology.setText(getString(R.string.battery_health_unknown));
            }

            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (temperature > 0) {
                float temp = ((float) temperature) / 10f;
                binding.moreInfoLayout.temperature.setText(temp + "°C");
            }

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (voltage > 0) {
                binding.moreInfoLayout.voltage.setText(voltage + " mV");
            }

            double capacity = getBatteryCapacity(requireContext());

            if (capacity > 0) {
                binding.moreInfoLayout.capacity.setText(capacity + " mAh");
            }

        } else {
            Toast.makeText(requireContext(), R.string.no_battery, Toast.LENGTH_SHORT).show();
        }
    }

    /*public long getBatteryCapacity(Context context) {
        BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int chargeCounter = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        int capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if(chargeCounter == Integer.MIN_VALUE || capacity == Integer.MIN_VALUE) return 0;

        return (chargeCounter/capacity) * 100L;
    }*/

    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}