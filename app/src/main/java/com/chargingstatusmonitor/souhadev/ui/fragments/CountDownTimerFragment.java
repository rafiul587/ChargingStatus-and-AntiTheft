package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.TOUCH_ALARM;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.utils.Constants;

import java.util.Objects;

public class CountDownTimerFragment extends DialogFragment {

    private TextView countdownText;
    private String alarmType;
    private CountDownTimer countDownTimer;
    AppDataStore dataStore;

    public static CountDownTimerFragment newInstance(String value) {
        CountDownTimerFragment fragment = new CountDownTimerFragment();
        Bundle args = new Bundle();
        args.putString("alarmType", value);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_count_timer, null);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        countdownText = view.findViewById(R.id.counter);
        if (getArguments() != null) {
            alarmType = getArguments().getString("alarmType");
        }

        countDownTimer = new CountDownTimer(Constants.COUNTER_TIMER, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d("TAG", "onFinish: " + alarmType + TOUCH_ALARM.getName());
                if (Objects.equals(alarmType, TOUCH_ALARM.getName())) {
                    dataStore.saveBooleanValue(AppDataStore.TOUCH_ALARM, true);
                } else if (Objects.equals(alarmType, ANTI_POCKET_ALARM.getName())) {
                    dataStore.saveBooleanValue(ANTI_POCKET_ALARM, true);
                } else if (Objects.equals(alarmType, ANTI_THEFT_PROTECTION.getName())) {
                    dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, true);
                }
                dismiss();
            }
        };
        setCancelable(false);
        countDownTimer.start();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setView(view);

        return builder.create();
    }

    private void updateCountdownText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        countdownText.setText(String.valueOf(seconds + 1));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
    }
}

