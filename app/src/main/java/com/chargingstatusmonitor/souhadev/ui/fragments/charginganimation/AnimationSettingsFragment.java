package com.chargingstatusmonitor.souhadev.ui.fragments.charginganimation;

import static com.chargingstatusmonitor.souhadev.AppDataStore.CLOSE_METHOD;
import static com.chargingstatusmonitor.souhadev.AppDataStore.PLAY_DURATION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SHOW_BATTERY_PERCENTAGE;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SHOW_ON_LOCK_SCREEN;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SOUND_WITH_ANIMATION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentAnimationSettingsBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        binding.soundWithAnimationSwitch.setChecked(dataStore.getBoolean(SOUND_WITH_ANIMATION, false).blockingFirst());
        binding.batteryPercentageSwitch.setChecked(dataStore.getBoolean(SHOW_BATTERY_PERCENTAGE, true).blockingFirst());
        binding.showOnLockScreenSwitch.setChecked(dataStore.getBoolean(SHOW_ON_LOCK_SCREEN, false).blockingFirst());
/*        disposable.add(dataStore.shouldShowBatteryPercentage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enabled -> binding.showOnLockScreenSwitch.setChecked(enabled)))*/;
        binding.playDurationSpinner.setSelection(dataStore.getIntegerValue(PLAY_DURATION).blockingFirst());
        binding.closeMethodSpinner.setSelection(dataStore.getIntegerValue(CLOSE_METHOD).blockingFirst());
        binding.soundWithAnimationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStore.saveBooleanValue(SOUND_WITH_ANIMATION, isChecked);
        });
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