package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ALARM_CLOSING_PIN;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CHARGING_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CHARGING_ANIMATION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.TOUCH_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.UNPLUGGED_ALARM;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.ui.activities.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentHomeBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private CompositeDisposable disposable;

    AppDataStore dataStore;
    private String alarmClosingPin = "";
    private boolean isAntiPocketAlarmEnabled = false;
    private boolean isTouchAlarmEnabled = false;
    private boolean isUnpluggedAlarmEnabled = false;

    private List<String> selectedAlarmList;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        selectedAlarmList = new ArrayList<>();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.chargingAlarmSwitch.setChecked(dataStore.getBoolean(CHARGING_ALARM, false).blockingFirst());
        binding.chargingAnimationSwitch.setChecked(dataStore.getBoolean(CHARGING_ANIMATION, true).blockingFirst());
        binding.antiTheftProtectionSwitch.setChecked(dataStore.getBoolean(ANTI_THEFT_PROTECTION, false).blockingFirst());
        disposable.add(dataStore.getBoolean(UNPLUGGED_ALARM, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    isUnpluggedAlarmEnabled = value;
                    Log.d("TAG", "onCreateView: unplug" + value + "," + selectedAlarmList);
                    updateAlarmsText();
                }));
        disposable.add(dataStore.getBoolean(TOUCH_ALARM, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    isTouchAlarmEnabled = value;
                    Log.d("TAG", "onCreateView: touch" + value + "," + selectedAlarmList);
                    updateAlarmsText();
                }));
        disposable.add(dataStore.getBoolean(ANTI_POCKET_ALARM, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    isAntiPocketAlarmEnabled = value;
                    Log.d("TAG", "onCreateView: antipocket" + value + "," + selectedAlarmList);
                    updateAlarmsText();
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
                Toast.makeText(requireContext(), getString(R.string.set_pin_first), Toast.LENGTH_SHORT).show();
                ((MainActivity) requireActivity()).goToAntiTheftSection();
                buttonView.setChecked(false);
                return;
            }

            if (buttonView.isPressed()) {
                if (isChecked) {
                    if (isTouchAlarmEnabled || isAntiPocketAlarmEnabled) {
                        CountDownTimerFragment dialogFragment = CountDownTimerFragment.newInstance(ANTI_THEFT_PROTECTION.getName());
                        dialogFragment.show(getChildFragmentManager(), "timer_dialog");
                    } else dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, true);
                } else {
                    dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, false);
                }
            } else {
                dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, isChecked);
            }
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

    public void setServiceSwitchInitialState() {
        binding.startOrStopServiceSwitch.setChecked(true);
    }

    private void updateAlarmsText() {
        selectedAlarmList.clear();
        if (isUnpluggedAlarmEnabled) {
            selectedAlarmList.add(getString(R.string.unplugged_alarm));
        }
        if (isTouchAlarmEnabled) {
            selectedAlarmList.add(getString(R.string.touch_alarm));
        }
        if (isAntiPocketAlarmEnabled) {
            selectedAlarmList.add(getString(R.string.anti_pocket_alarm));
        }
        if (selectedAlarmList.isEmpty()) {
            binding.selectedAlarms.setText(R.string.no_protection);
        } else {
            binding.selectedAlarms.setText(String.join(", ", selectedAlarmList));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }
}