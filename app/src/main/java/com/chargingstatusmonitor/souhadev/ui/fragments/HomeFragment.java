package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.AppDataStore.ALARM_CLOSING_PIN;
import static com.chargingstatusmonitor.souhadev.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.CHARGING_ALARM;
import static com.chargingstatusmonitor.souhadev.AppDataStore.CHARGING_ANIMATION;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentHomeBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private CompositeDisposable disposable;

    AppDataStore dataStore;
    private String alarmClosingPin = "";

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.chargingAlarmSwitch.setChecked(dataStore.getBoolean(CHARGING_ALARM, false).blockingFirst());
        binding.chargingAnimationSwitch.setChecked(dataStore.getBoolean(CHARGING_ANIMATION, true).blockingFirst());
        disposable.add(dataStore.getBoolean(ANTI_THEFT_PROTECTION, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    binding.antiTheftProtectionSwitch.setChecked(value);
                }));
        alarmClosingPin = dataStore.getStringValue(ALARM_CLOSING_PIN).blockingFirst();
        binding.chargingAlarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(CHARGING_ALARM, isChecked);
        });
        binding.chargingAnimationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(CHARGING_ANIMATION, isChecked);
        });
        binding.antiTheftProtectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && alarmClosingPin.isEmpty()) {
                Toast.makeText(requireContext(), "You have to set a pin first!", Toast.LENGTH_SHORT).show();
                ((MainActivity) requireActivity()).goToAntiTheftSection();
                buttonView.setChecked(false);
                return;
            }
            dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, isChecked);
        });
        binding.startOrStopServiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = new Intent(requireContext(), MyService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(intent);
                } else requireContext().startService(intent);

            } else {
                requireContext().stopService(new Intent(requireContext(), MyService.class));
            }
        });
        return binding.getRoot();
    }

/*    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disposable.add(dataStore.getOnConnectFile()
                .subscribeOn(Schedulers.io()) // Emit values on an I/O thread// Receive values on the main thread
                .subscribe(connectFile -> {
                    // Handle each emitted value here on the main thread
                    handler.post(() -> {
                        binding.connectFile.setText(connectFile.split("-")[2]);
                    });
                }));
        disposable.add(dataStore.getOnDisconnectFile()
                .subscribeOn(Schedulers.io()) // Emit values on an I/O thread// Receive values on the main thread
                .subscribe(disconnectFile -> {
                    // Handle each emitted value here on the main thread
                    handler.post(() -> {
                        binding.disconnectFile.setText(disconnectFile.split("-")[2]);
                    });
                }));
    }*/

    public void setServiceSwitchInitialState() {
        binding.startOrStopServiceSwitch.setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.dispose();
        binding = null;
    }
}