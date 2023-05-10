package com.chargingstatusmonitor.souhadev.utils;

import static com.chargingstatusmonitor.souhadev.AppDataStore.ALARM_CLOSING_PIN;
import static com.chargingstatusmonitor.souhadev.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.CHARGING_ALARM;
import static com.chargingstatusmonitor.souhadev.AppDataStore.CHARGING_ANIMATION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.CLOSE_METHOD;
import static com.chargingstatusmonitor.souhadev.AppDataStore.DISCHARGING_ALARM;
import static com.chargingstatusmonitor.souhadev.AppDataStore.PLAY_DURATION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SELECTED_ANIMATION_NAME;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SHOW_BATTERY_PERCENTAGE;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SHOW_ON_LOCK_SCREEN;
import static com.chargingstatusmonitor.souhadev.AppDataStore.SOUND_WITH_ANIMATION;
import static com.chargingstatusmonitor.souhadev.AppDataStore.TOUCH_ALARM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.datastore.preferences.core.Preferences;

import com.chargingstatusmonitor.souhadev.AlarmActivity;
import com.chargingstatusmonitor.souhadev.AnimationActivity;
import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.LockScreenReceiver;
import com.chargingstatusmonitor.souhadev.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.PassCodeView;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.model.PreferenceModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyService extends Service implements SensorEventListener {

    private static final int NOTIFICATION_ID = 10001;
    MediaPlayer mediaPlayer;
    private PowerDisconnectedReceiver disconnectedReceiver;
    private PowerConnectedReceiver connectedReceiver;
    private CompositeDisposable disposable = new CompositeDisposable();
    private SensorManager sensorManager;
    private static final float THRESHOLD = 2.8f;
    private float[] gravity;
    private float[] linearAcceleration;

    float rp = -1;
    float rl = -1;
    float[] g = {0, 0, 0};
    int inclination = -1;

    int pocket = 0;

    AppDataStore dataStore;

    private boolean isChargingAnimationEnabled = true;
    private boolean isPluggedAlarmEnabled = false;
    private boolean isUnpluggedAlarmEnabled = true;
    private boolean isTouchAlarmEnabled = true;
    private boolean isAntiPocketAlarmEnabled = true;
    private boolean isAntiTheftAlarmEnabled = false;
    private boolean isSoundWithAnimationEnabled = true;
    private boolean isShowBatteryPercentageEnabled = true;
    private boolean isShowOnLockScreenEnabled = false;
    private String alarmClosingPing = "";
    private String selectedAnimation = "";
    private int playDuration = 0;
    private int closeMethod = 0;
    private PreferenceModel connectPreference;
    private PreferenceModel disconnectPreference;
    private final boolean shouldPlayAlarm = false;
    boolean isPocket = false;

    LockScreenReceiver lockScreenReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataStore = ((MyApplication) getApplicationContext()).getDataStore();
        disposable = new CompositeDisposable();
        createNotificationChannel();
        Flowable<String> onConnectFileObservable = dataStore.getOnConnectFile();
        Flowable<String> onDisconnectFileObservable = dataStore.getOnDisconnectFile();
        disposable.add(getBooleanDisposable(CHARGING_ANIMATION, true));
        disposable.add(getBooleanDisposable(CHARGING_ALARM, false));
        disposable.add(getBooleanDisposable(DISCHARGING_ALARM, true));
        disposable.add(getBooleanDisposable(TOUCH_ALARM, true));
        disposable.add(getBooleanDisposable(ANTI_POCKET_ALARM, true));
        disposable.add(getBooleanDisposable(ANTI_THEFT_PROTECTION, false));
        disposable.add(getBooleanDisposable(SOUND_WITH_ANIMATION, false));
        disposable.add(getBooleanDisposable(SHOW_BATTERY_PERCENTAGE, true));
        disposable.add(getBooleanDisposable(SHOW_ON_LOCK_SCREEN, true));
        disposable.add(getStringDisposable(ALARM_CLOSING_PIN));
        disposable.add(getStringDisposable(SELECTED_ANIMATION_NAME));
        disposable.add(getIntegerDisposable(PLAY_DURATION));
        disposable.add(getIntegerDisposable(CLOSE_METHOD));

        Flowable<Pair<PreferenceModel, PreferenceModel>> observable = Flowable.zip(onConnectFileObservable,
                onDisconnectFileObservable,
                (connect, disconnect) -> {
                    String[] connectSplits = connect.split(Constants.SPLITTER);
                    String connectType = connectSplits[0];
                    String path = connectSplits[1];
                    String connectFile = connectSplits[2];
                    PreferenceModel connectFileModel = new PreferenceModel(connectFile, connectType, path);

                    String[] disconnectSplits = disconnect.split(Constants.SPLITTER);
                    String disconnectType = disconnectSplits[0];
                    String disconnectPath = disconnectSplits[1];
                    String disconnectFile = disconnectSplits[2];
                    PreferenceModel disconnectFileModel = new PreferenceModel(disconnectFile, disconnectType, disconnectPath);

                    return new Pair<>(connectFileModel, disconnectFileModel);
                });

        Disposable soundFileDisposable = observable
                .subscribe(pair -> {
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        Log.d("TAG", "onStartCommand: " + pair.first.getName() + ", " + pair.second.getName());
                        connectPreference = pair.first;
                        disconnectPreference = pair.second;
                    });
                });
        disposable.add(soundFileDisposable);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        gravity = new float[3];
        g = new float[3];
        linearAcceleration = new float[3];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            resetMediaPlayer();
            if (intent.getAction().equals("alarm_removed")) {
                dataStore.saveBooleanValue(ANTI_THEFT_PROTECTION, false);
            }
        }
        Notification notification = createNotification();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        resetMediaPlayer();
        unregisterPowerConnectedReceiver();
        unregisterPowerDisconnectedReceiver();
        sensorManager.unregisterListener(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Charging Status Monitor")
                .setContentText("Service is running for anti-theft abd charging animations")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(createPendingIntent());
        return builder.build();
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }


    private boolean isAlarmShowing = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float acceleration = (float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);

            if (acceleration > THRESHOLD) {
                // Do something here when movement or displacement is detected
                if (!isAlarmShowing) {
                    showAlarmScreen();
                    isAlarmShowing = true;
                }
                Log.d("MainActivity", "Device moved!");
            } else {
                isAlarmShowing = false;
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linearAcceleration[0] = event.values[0] - gravity[0];
            linearAcceleration[1] = event.values[1] - gravity[1];
            linearAcceleration[2] = event.values[2] - gravity[2];

            float acceleration = (float) Math.sqrt(linearAcceleration[0] * linearAcceleration[0] + linearAcceleration[1] * linearAcceleration[1] + linearAcceleration[2] * linearAcceleration[2]);

            if (acceleration > THRESHOLD) {
                // Do something here when movement or displacement is detected
                Log.d("MainActivity", "Device moved!");
            }

            g = event.values.clone();

            double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

            g[0] = (float) (g[0] / norm_Of_g);
            g[1] = (float) (g[1] / norm_Of_g);
            g[2] = (float) (g[2] / norm_Of_g);

            inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
            Log.d("TAG", "onSensorChanged: XYZ " + round(g[0]) + ",  " + round(g[1]) + ",  " + round(g[2]) + "  inc: " + inclination);
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            //Log.d("TAG", "onSensorChanged: " +event.values[0] + shouldPlayAlarm + isPocket);
            if (isPocket) {
                resetMediaPlayer();
                showAlarmScreen();
            }
            isPocket = event.values[0] <= 1;
        }

        /*if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.d("TAG", "onSensorChanged: " + event.values[0] + shouldPlayAlarm + isPocket);
            if (event.values[0] < 5 && isPocket) {
                shouldPlayAlarm = true;
            } else {

            }
        }*/
    }

    public BigDecimal round(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd;
    }


    private void registerPowerConnectedReceiver() {
        if (connectedReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            connectedReceiver = new PowerConnectedReceiver();
            registerReceiver(connectedReceiver, intentFilter);
        }
    }

    private void unregisterPowerConnectedReceiver() {
        try {
            unregisterReceiver(connectedReceiver);
            connectedReceiver = null;
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void registerPowerDisconnectedReceiver() {
        if (disconnectedReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            disconnectedReceiver = new PowerDisconnectedReceiver();
            registerReceiver(disconnectedReceiver, intentFilter);
        }
    }

    private void unregisterPowerDisconnectedReceiver() {
        try {
            unregisterReceiver(disconnectedReceiver);
            disconnectedReceiver = null;
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void showAnimationScreen() {
        Intent animationIntent = new Intent(this, AnimationActivity.class);
        animationIntent.putExtra("isBatteryPercentageEnabled", isShowBatteryPercentageEnabled);
        animationIntent.putExtra("close_method", closeMethod);
        animationIntent.putExtra("play_duration", playDuration);
        animationIntent.putExtra("selected_animation", selectedAnimation);
        animationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(animationIntent);
    }

    public void showAlarmScreen() {
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        alarmIntent.putExtra("alarm_closing_pin", alarmClosingPing);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmIntent);

        try {
            resetMediaPlayer();
            mediaPlayer = new MediaPlayer();
            String fileName = getString(R.string.alarm_folder_name) + "/alarm.mp3";
            Log.d("TAG", "onReceive: " + fileName);
            AssetFileDescriptor descriptor = getAssets().openFd(fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(descriptor);
            } else {
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            }
            descriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            Log.d("TAG", "onReceive: " + e.getMessage());
        }
    }

    public void makeFullScreenWindow(View overlayView) {
        int overlayType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // Set any necessary attributes to your view
        int windowFlags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, overlayType, windowFlags, PixelFormat.TRANSLUCENT);
        overlayView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsetsController insetsController = overlayView.getWindowInsetsController();
                    if (insetsController != null) {
                        insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    }
                } else {
                    // Add the deprecated FLAG_FULLSCREEN flag for versions below API level 30
                    layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                }

                // Remove the OnGlobalLayoutListener to prevent multiple calls
                overlayView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    public Disposable getBooleanDisposable(Preferences.Key<Boolean> key, Boolean defaultValue) {
        return dataStore.getBoolean(key, defaultValue).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(value -> {
            if (CHARGING_ANIMATION.equals(key)) {
                isChargingAnimationEnabled = value;
                Log.d("TAG", "getBooleanDisposable: " + value);
                if (value) {
                    registerPowerConnectedReceiver();
                } else if (!isPluggedAlarmEnabled) unregisterPowerConnectedReceiver();
            } else if (CHARGING_ALARM.equals(key)) {
                isPluggedAlarmEnabled = value;
                if (value) {
                    registerPowerConnectedReceiver();
                } else {
                    if (!isChargingAnimationEnabled) unregisterPowerConnectedReceiver();
                }
            } else if (DISCHARGING_ALARM.equals(key)) {
                isUnpluggedAlarmEnabled = value;
                if (value) {
                    if (isAntiTheftAlarmEnabled) registerPowerDisconnectedReceiver();
                } else unregisterPowerDisconnectedReceiver();
            } else if (TOUCH_ALARM.equals(key)) {
                isTouchAlarmEnabled = value;
                handleOnTouchAlarm();
            } else if (ANTI_POCKET_ALARM.equals(key)) {
                isAntiPocketAlarmEnabled = value;
                handleAntiPocketAlarm();
            } else if (ANTI_THEFT_PROTECTION.equals(key)) {
                isAntiTheftAlarmEnabled = value;
                if (value) {
                    if (isUnpluggedAlarmEnabled) registerPowerDisconnectedReceiver();
                } else {
                    unregisterPowerDisconnectedReceiver();
                }
                handleOnTouchAlarm();
                handleAntiPocketAlarm();
            } else if (SOUND_WITH_ANIMATION.equals(key)) {
                isSoundWithAnimationEnabled = value;
            } else if (SHOW_BATTERY_PERCENTAGE.equals(key)) {
                isShowBatteryPercentageEnabled = value;
            } else if (SHOW_ON_LOCK_SCREEN.equals(key)) {
                isShowOnLockScreenEnabled = value;
                showAnimationOnLockScreen(value);
            }
        });
    }


    public Disposable getStringDisposable(Preferences.Key<String> key) {
        return dataStore.getStringValue(key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(value -> {
            if (ALARM_CLOSING_PIN.equals(key)) {
                alarmClosingPing = value;
            } else if (SELECTED_ANIMATION_NAME.equals(key)) {
                selectedAnimation = value.isEmpty() ? "default.gif" : value;
            }
        });
    }

    public Disposable getIntegerDisposable(Preferences.Key<Integer> key) {
        return dataStore.getIntegerValue(key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(value -> {
            if (PLAY_DURATION.equals(key)) {
                switch (value) {
                    case 0:
                        playDuration = 3;
                        break;
                    case 1:
                        playDuration = 5;
                        break;
                    case 2:
                        playDuration = 15;
                        break;
                    case 3:
                        playDuration = 30;
                        break;
                    case 4:
                        playDuration = 60;
                        break;
                    default:
                        break;
                }
            } else if (CLOSE_METHOD.equals(key)) {
                closeMethod = value;
            }
        });
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void handleOnTouchAlarm() {
        Sensor linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (isAntiTheftAlarmEnabled && isTouchAlarmEnabled) {
            if (linearAccelerationSensor == null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                return;
            }
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            if (linearAccelerationSensor == null) {
                sensorManager.unregisterListener(this, accelerometerSensor);
                return;
            }
            sensorManager.unregisterListener(this, linearAccelerationSensor);
        }
    }

    private void handleAntiPocketAlarm() {
        Sensor proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (isAntiTheftAlarmEnabled && isAntiPocketAlarmEnabled) {
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.unregisterListener(this, proximity);
        }
    }

    public void onChargerDisconnected() {
        resetMediaPlayer();
        if (disconnectPreference == null) return;
        showAlarmScreen();
    }

    public void onChargerConnected() {
        resetMediaPlayer();
        if (connectPreference == null) return;
        if (isChargingAnimationEnabled) {
            showAnimationScreen();
            if (isSoundWithAnimationEnabled) {
                try {
                    mediaPlayer = new MediaPlayer();
                    if (Objects.equals(connectPreference.getType(), FileType.ASSET)) {
                        String fileName = getString(R.string.asset_folder_name) + "/" + connectPreference.getName();
                        Log.d("TAG", "onReceive: " + fileName);
                        AssetFileDescriptor descriptor = getAssets().openFd(fileName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mediaPlayer.setDataSource(descriptor);
                        } else {
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        }
                        descriptor.close();
                    } else {
                        Log.d("TAG", "onChargerConnected: " + connectPreference.getPath());
                        mediaPlayer.setDataSource(this, Uri.parse(connectPreference.getPath()));
                    }
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Log.d("TAG", "onReceive: " + e.getMessage());
                }
            }
        } else if (isPluggedAlarmEnabled) {
            showAlarmScreen();
        }
    }

    public void showAnimationOnLockScreen(boolean value) {
        lockScreenReceiver = new LockScreenReceiver();
        if (value) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(lockScreenReceiver, intentFilter);
        } else {
            try {
                unregisterReceiver(lockScreenReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    public void resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}