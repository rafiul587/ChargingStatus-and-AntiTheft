package com.chargingstatusmonitor.souhadev.ui.fragments.antitheft;

import static com.chargingstatusmonitor.souhadev.AppDataStore.TOUCH_ALARM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentTouchAlarmBinding;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TouchAlarmFragment extends Fragment {

    private FragmentTouchAlarmBinding binding;
    AppDataStore dataStore;

    public TouchAlarmFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTouchAlarmBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.activationSwitch.setChecked(dataStore.getBoolean(TOUCH_ALARM, true).blockingFirst());
        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(TOUCH_ALARM, isChecked);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}