package com.chargingstatusmonitor.souhadev;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.chargingstatusmonitor.souhadev.databinding.OverlayAlarmViewBinding;
import com.chargingstatusmonitor.souhadev.databinding.OverlayAnimationViewBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import java.io.File;

public class AlarmActivity extends AppCompatActivity {
    private Handler alarmHandler;
    private Runnable mColorRunnable;
    private String alarmClosingPing = "";
    private boolean mIsColor1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlayAlarmViewBinding binding = OverlayAlarmViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        alarmClosingPing = getIntent().getExtras().getString("alarm_closing_pin", "");
        makeFullScreenWindow();
        binding.passCodeView.setKeyTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        binding.passCodeView.setOnTextChangeListener(text -> {
            Log.d("TAG", "onTextChanged: " + text);
            if (text.length() == 4) {
                if (text.equals(alarmClosingPing)) {
                    finishAffinity();
                    Intent intent = new Intent(AlarmActivity.this, MyService.class);
                    intent.setAction("alarm_removed");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }startService(intent);
                } else {
                    binding.passCodeView.setError(true);
                }
            }
        });
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

    public void makeFullScreenWindow() {
        int overlayType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        int windowFlags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                overlayType,
                windowFlags,
                PixelFormat.TRANSLUCENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        }
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmHandler.removeCallbacks(mColorRunnable);
    }
}