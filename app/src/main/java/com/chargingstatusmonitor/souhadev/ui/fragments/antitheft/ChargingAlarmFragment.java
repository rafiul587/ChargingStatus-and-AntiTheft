package com.chargingstatusmonitor.souhadev.ui.fragments.antitheft;

import static com.chargingstatusmonitor.souhadev.AppDataStore.DISCHARGING_ALARM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentChargingAlarmBinding;
import com.chargingstatusmonitor.souhadev.databinding.FragmentSetPinBinding;

public class ChargingAlarmFragment extends Fragment {

    private FragmentChargingAlarmBinding binding;
    AppDataStore dataStore;

    public ChargingAlarmFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChargingAlarmBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.activationSwitch.setChecked(dataStore.getBoolean(DISCHARGING_ALARM, true).blockingFirst());
        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(DISCHARGING_ALARM, isChecked);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}