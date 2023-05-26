package com.chargingstatusmonitor.souhadev.ui.activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.OverlayAlarmViewBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.MyService;

public class AlarmActivity extends AppCompatActivity {
    private Handler alarmHandler;
    private Runnable mColorRunnable;
    private String alarmClosingPing = "";
    private int sensorType = -1;
    private boolean mIsColor1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlayAlarmViewBinding binding = OverlayAlarmViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alarmClosingPing = getIntent().getExtras().getString(Constants.KEY_ALARM_CLOSING_PIN, "");
        sensorType = getIntent().getExtras().getInt(Constants.KEY_TYPE, -1);

        displaySensorToast();
        makeFullScreenWindow();
        setPassCodeKeyTextColor(binding);
        setPassCodeChangeListener(binding);
        startColorRunnable(binding);

    }

    private void setPassCodeKeyTextColor(OverlayAlarmViewBinding binding) {
        binding.passCodeView.setKeyTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
    }

    private void setPassCodeChangeListener(OverlayAlarmViewBinding binding) {
        binding.passCodeView.setOnTextChangeListener(text -> {
            if (text.length() == 4) {
                if (text.equals(alarmClosingPing)) {
                    finish();
                    Intent intent = new Intent(AlarmActivity.this, MyService.class);
                    intent.setAction(String.valueOf(sensorType));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else startService(intent);
                } else {
                    binding.passCodeView.setError(true);
                }
            }
        });
    }

    private void startColorRunnable(OverlayAlarmViewBinding binding) {
        alarmHandler = new Handler(Looper.getMainLooper());
        mColorRunnable = new Runnable() {
            @Override
            public void run() {
                // Alternate the background color of the view
                if (mIsColor1) {
                    binding.getRoot().setBackgroundColor(getResources().getColor(R.color.green_200));
                } else {
                    binding.getRoot().setBackgroundColor(getResources().getColor(R.color.red));
                }
                mIsColor1 = !mIsColor1;

                // Post the runnable again to keep alternating the colors
                alarmHandler.postDelayed(this, 1000);
            }
        };

        // Start alternating the colors
        alarmHandler.post(mColorRunnable);
    }

    private void displaySensorToast() {
        if (sensorType == Sensor.TYPE_PROXIMITY) {
            Toast.makeText(this, getString(R.string.msg_anti_pocket_alarm), Toast.LENGTH_SHORT).show();
        } else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            Toast.makeText(this, getString(R.string.msg_touch_alarm), Toast.LENGTH_SHORT).show();
        } else if (sensorType == Constants.ALARM_TYPE_PLUGGED) {
            Toast.makeText(this, R.string.msg_charger_plugged_alarm, Toast.LENGTH_SHORT).show();
        } else if (sensorType == Constants.ALARM_TYPE_UNPLUGGED) {
            Toast.makeText(this, R.string.msg_charger_unplugged_alarm, Toast.LENGTH_SHORT).show();
        }
    }

    public void makeFullScreenWindow() {
        int overlayType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        int windowFlags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, overlayType, windowFlags, PixelFormat.TRANSLUCENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        }
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmHandler.removeCallbacks(mColorRunnable);
    }
}