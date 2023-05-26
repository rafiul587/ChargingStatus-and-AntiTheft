package com.chargingstatusmonitor.souhadev.ui.antitheft;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_THEFT_PROTECTION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.databinding.FragmentAntiPocketAlarmBinding;
import com.chargingstatusmonitor.souhadev.ui.fragments.CountDownTimerFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AntiPocketAlarmFragment extends Fragment {

    private FragmentAntiPocketAlarmBinding binding;
    CompositeDisposable disposable;
    AppDataStore dataStore;
    private boolean isAntiTheftProtectionEnabled = false;

    public AntiPocketAlarmFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAntiPocketAlarmBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        disposable.add(dataStore.getBoolean(ANTI_THEFT_PROTECTION, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    isAntiTheftProtectionEnabled = value;
                }));
        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                if (isChecked) {
                    if (isAntiTheftProtectionEnabled) {
                        CountDownTimerFragment dialogFragment = CountDownTimerFragment.newInstance(ANTI_POCKET_ALARM.getName());
                        dialogFragment.show(getChildFragmentManager(), "timer_dialog");
                    } else dataStore.saveBooleanValue(ANTI_POCKET_ALARM, true);
                } else {
                    dataStore.saveBooleanValue(ANTI_POCKET_ALARM, false);
                }
            } else {
                dataStore.saveBooleanValue(ANTI_POCKET_ALARM, isChecked);
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.activationSwitch.setChecked(dataStore.getBoolean(ANTI_POCKET_ALARM, false).blockingFirst());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }
}