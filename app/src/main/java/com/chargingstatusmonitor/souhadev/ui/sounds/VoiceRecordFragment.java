package com.chargingstatusmonitor.souhadev.ui.sounds;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chargingstatusmonitor.souhadev.utils.AppExecutors;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentVoiceRecordBinding;
import com.chargingstatusmonitor.souhadev.utils.VoiceRecorder;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class VoiceRecordFragment extends Fragment {

    private FragmentVoiceRecordBinding binding;

    private Timer mTimer;
    private long mRecordingStartTime;
    private VoiceRecorder recorder;
    CountDownTimer countDownTimer;
    long counter = 0L;

    public VoiceRecordFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVoiceRecordBinding.inflate(inflater, container, false);
        recorder = new VoiceRecorder(requireContext());
        countDownTimer = new CountDownTimer(1200000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter++;
                long millis = (counter % 10) * 10;
                long minutes = (counter / 10) / 60;
                long seconds = (counter / 10) % 60;
                String duration = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, millis);
                binding.timer.setText(duration);
            }

            @Override
            public void onFinish() {
                startRecording();
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

        //mTimer = new Timer();
        if (recorder != null) recorder.onRecord(true);
        if (countDownTimer != null) countDownTimer.start();
        /*mRecordingStartTime = System.currentTimeMillis();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    updateTimerText();
                });
            }
        }, 0, 100);*/
    }

    private void stopRecording() {
        if (countDownTimer != null) countDownTimer.cancel();
        counter = 0;
        if (recorder != null) recorder.onRecord(false);
        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        binding.timer.setText("00:00:00");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}