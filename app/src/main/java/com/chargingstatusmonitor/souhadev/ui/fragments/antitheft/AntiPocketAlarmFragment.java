package com.chargingstatusmonitor.souhadev.ui.fragments.antitheft;

import static com.chargingstatusmonitor.souhadev.AppDataStore.ANTI_POCKET_ALARM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentAntiPocketAlarmBinding;

public class AntiPocketAlarmFragment extends Fragment {

    private FragmentAntiPocketAlarmBinding binding;
    AppDataStore dataStore;

    public AntiPocketAlarmFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAntiPocketAlarmBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.activationSwitch.setChecked(dataStore.getBoolean(ANTI_POCKET_ALARM, true).blockingFirst());
        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(ANTI_POCKET_ALARM, isChecked);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}