package com.chargingstatusmonitor.souhadev.ui.charginganimation;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CLOSE_METHOD;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.PLAY_DURATION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.SHOW_BATTERY_PERCENTAGE;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.SHOW_ON_LOCK_SCREEN;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.databinding.FragmentAnimationSettingsBinding;

public class AnimationSettingsFragment extends Fragment {

    private FragmentAnimationSettingsBinding binding;

    AppDataStore dataStore;

    public AnimationSettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnimationSettingsBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.batteryPercentageSwitch.setChecked(dataStore.getBoolean(SHOW_BATTERY_PERCENTAGE, true).blockingFirst());
        binding.showOnLockScreenSwitch.setChecked(dataStore.getBoolean(SHOW_ON_LOCK_SCREEN, false).blockingFirst());
        binding.playDurationSpinner.setSelection(dataStore.getIntegerValue(PLAY_DURATION, 0).blockingFirst());
        binding.closeMethodSpinner.setSelection(dataStore.getIntegerValue(CLOSE_METHOD, 1).blockingFirst());
        binding.batteryPercentageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(AppDataStore.SHOW_BATTERY_PERCENTAGE, isChecked);
        });
        binding.showOnLockScreenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(AppDataStore.SHOW_ON_LOCK_SCREEN, isChecked);
        });
        binding.playDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataStore.saveIntegerValue(AppDataStore.PLAY_DURATION, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.closeMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataStore.saveIntegerValue(AppDataStore.CLOSE_METHOD, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}