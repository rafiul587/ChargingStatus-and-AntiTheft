package com.chargingstatusmonitor.souhadev.ui.antitheft;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.TOUCH_ALARM;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentTouchAlarmBinding;
import com.chargingstatusmonitor.souhadev.ui.fragments.CountDownTimerFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TouchAlarmFragment extends Fragment {

    private FragmentTouchAlarmBinding binding;
    CompositeDisposable disposable;
    AppDataStore dataStore;
    private boolean isAntiTheftProtectionEnabled = false;

    public TouchAlarmFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTouchAlarmBinding.inflate(inflater, container, false);
        disposable = new CompositeDisposable();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.activationSwitch.setVisibility(View.VISIBLE);
        binding.counterView.setVisibility(View.GONE);
        binding.activationText.setVisibility(View.GONE);

        disposable.add(dataStore.getBoolean(ANTI_THEFT_PROTECTION, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    isAntiTheftProtectionEnabled = value;
                }));

        binding.activationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("TAG", "onCreateView: " + isChecked);
            if (buttonView.isPressed()) {
                if (isChecked) {
                    if (isAntiTheftProtectionEnabled) {
                        CountDownTimerFragment dialogFragment = CountDownTimerFragment.newInstance(TOUCH_ALARM.getName());
                        dialogFragment.show(getChildFragmentManager(), "timer_dialog");
                    } else dataStore.saveBooleanValue(TOUCH_ALARM, true);
                } else {
                    dataStore.saveBooleanValue(TOUCH_ALARM, false);
                }
            } else {
                dataStore.saveBooleanValue(TOUCH_ALARM, isChecked);
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.activationSwitch.setChecked(dataStore.getBoolean(TOUCH_ALARM, false).blockingFirst());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }
}