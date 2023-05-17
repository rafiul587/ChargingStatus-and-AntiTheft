package com.chargingstatusmonitor.souhadev.ui.antitheft;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.UNPLUGGED_ALARM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.databinding.FragmentUnpluggedAlarmBinding;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class UnpluggedAlarmFragment extends Fragment {

    private FragmentUnpluggedAlarmBinding binding;
    AppDataStore dataStore;
    CompositeDisposable disposable;

    public UnpluggedAlarmFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUnpluggedAlarmBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();

        binding.activationSwitch.setChecked(dataStore.getBoolean(UNPLUGGED_ALARM, false).blockingFirst());
        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(UNPLUGGED_ALARM, isChecked);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }
}