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

import com.bumptech.glide.Glide;
import com.chargingstatusmonitor.souhadev.databinding.OverlayAnimationViewBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import java.io.File;

public class AnimationActivity extends AppCompatActivity {
    private Handler animationHandler;
    Runnable animationRunnable;

    private String selectedAnimation = "";
    private int playDuration = 0;
    private int closeMethod = 0;
    private boolean isShowBatteryPercentageEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlayAnimationViewBinding binding = OverlayAnimationViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isShowBatteryPercentageEnabled = getIntent().getExtras().getBoolean("isBatteryPercentageEnabled", true);
        closeMethod = getIntent().getExtras().getInt("close_method", 0);
        playDuration = getIntent().getExtras().getInt("play_duration", 3);
        selectedAnimation = getIntent().getExtras().getString("selected_animation", "default.gif");
        Log.d("TAG", "onCreate: " +selectedAnimation);
        makeFullScreenWindow();
        if (selectedAnimation.equals("default.gif")) {
            Glide.with(this)
                    .asGif()
                    .load("file:///android_asset/animations/default.gif")
                    .into(binding.animationView);
        } else {
            File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.download_folder_name));
            File[] files = storagePath.listFiles((dir, name) -> name.equals(selectedAnimation));
            if (files != null && files.length > 0) {
                Glide.with(this).asGif().load(files[0]).into(binding.animationView);
            }

            if (isShowBatteryPercentageEnabled) {
                BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
                int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                binding.batteryPercentage.setText(batteryLevel + "%");
                Shader shader = new LinearGradient(0, 0, 0, binding.batteryPercentage.getTextSize(), Color.parseColor("#BDFFC3"), Color.parseColor("#3E963F"), Shader.TileMode.CLAMP);
                binding.batteryPercentage.getPaint().setShader(shader);
            }
        }

        if (closeMethod == 0) {
            binding.closeInstruction.setText("Tap to close the screen");
        } else {
            binding.closeInstruction.setText("Double Tap to close the screen");
        }

        animationHandler = new Handler(Looper.getMainLooper());

        animationRunnable = this::finish;
        binding.getRoot().getRootView().setOnTouchListener(new View.OnTouchListener() {

            private final GestureDetector gestureDetector = new GestureDetector(AnimationActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (closeMethod == 1) {
                        finish();
                        Intent intent = new Intent(AnimationActivity.this, MyService.class);
                        intent.setAction("animation_removed");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        }
                        startService(intent);
                    }
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                    if (closeMethod == 0) {
                        finish();
                    }
                    return super.onSingleTapConfirmed(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        animationHandler.postDelayed(animationRunnable, (playDuration * 1000L));
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
        animationHandler.removeCallbacks(animationRunnable);
    }
}