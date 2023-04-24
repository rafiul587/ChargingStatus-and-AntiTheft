package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.RECORD_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.MainActivity;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentVoiceRecordBinding;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;
import com.chargingstatusmonitor.souhadev.utils.VoiceRecorder;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class VoiceRecordFragment extends Fragment {

    private FragmentVoiceRecordBinding binding;

    private Timer mTimer;
    private long mRecordingStartTime;
    private VoiceRecorder recorder;

    public VoiceRecordFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVoiceRecordBinding.inflate(inflater, container, false);
        recorder = new VoiceRecorder(requireContext());

        AtomicLong counter = new AtomicLong(0L);

        CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter.getAndIncrement();
                long millis = (counter.get() % 10) * 10;
                long minutes = (counter.get() / 10) / 60;
                long seconds = (counter.get() / 10) % 60;
                binding.timer.setText(String.format("%02d:%02d:%02d", minutes, seconds, millis));
            }

            @Override
            public void onFinish() {

            }
        };

        binding.record.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecording();
                    return true;
            }
            return false;
        });

        return binding.getRoot();
    }


    private void startRecording() {
        mRecordingStartTime = System.currentTimeMillis();
        mTimer = new Timer();
        if (recorder != null) recorder.onRecord(true);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    updateTimerText();
                });
            }
        }, 0, 100);
    }

    private void stopRecording() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (recorder != null) recorder.onRecord(false);
        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        binding.timer.setText("00:00:00");
    }

    private void updateTimerText() {
        long durationInMillis = System.currentTimeMillis() - mRecordingStartTime;
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
                - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)
                - TimeUnit.HOURS.toSeconds(hours)
                - TimeUnit.MINUTES.toSeconds(minutes);

        long millis = TimeUnit.MILLISECONDS.toMillis(durationInMillis)
                - TimeUnit.HOURS.toMillis(hours)
                - TimeUnit.MINUTES.toMillis(minutes)
                - TimeUnit.SECONDS.toMillis(seconds);

        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, millis / 10);
        binding.timer.setText(time);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}