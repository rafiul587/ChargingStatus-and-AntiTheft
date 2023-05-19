package com.chargingstatusmonitor.souhadev.utils;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ALARM_CLOSING_PIN;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_POCKET_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ANTI_THEFT_PROTECTION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CHARGING_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CHARGING_ANIMATION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.CLOSE_METHOD;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.UNPLUGGED_ALARM;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.PLAY_DURATION;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.SELECTED_ANIMATION_NAME;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.SHOW_BATTERY_PERCENTAGE;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.SHOW_ON_LOCK_SCREEN;
import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.TOUCH_ALARM;
import static com.chargingstatusmonitor.souhadev.utils.Constants.CHANNEL_ID;
import static com.chargingstatusmonitor.souhadev.utils.Constants.MOVEMENT_THRESHOLD;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.NotificationCompat;
import androidx.datastore.preferences.core.Preferences;

import com.chargingstatusmonitor.souhadev.ui.activities.AlarmActivity;
import com.chargingstatusmonitor.souhadev.ui.activities.AnimationActivity;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.ui.activities.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.model.PreferenceModel;

import java.io.IOException;
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

    private float[] gravity;
    private float[] linearAcceleration;
    float[] g = {0, 0, 0};
    AppDataStore dataStore;

    private boolean isChargingAnimationEnabled = true;
    private boolean isPluggedAlarmEnabled = false;
    private boolean isUnpluggedAlarmEnabled = true;
    private boolean isTouchAlarmEnabled = true;
    private boolean isAntiPocketAlarmEnabled = true;
    private boolean isAntiTheftAlarmEnabled = false;
    private boolean isShowBatteryPercentageEnabled = true;
    Sensor proximity;
    private String alarmClosingPing = "";
    private String selectedAnimation = "";
    private int playDuration = 0;
    private int closeMethod = 0;
    private PreferenceModel connectPreference;
    private PreferenceModel disconnectPreference;

    Vibrator vibrator;
    VibratorManager vibratorManager;

    private boolean isAlarmShowing = false;
    boolean isPocket = false;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private final Object lock = new Object();


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
        if (Build.VERSION.SDK_INT >= 31) {
            vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        Flowable<String> onConnectFileObservable = dataStore.getOnConnectFile();
        Flowable<String> onDisconnectFileObservable = dataStore.getOnDisconnectFile();
        disposable.add(getBooleanDisposable(CHARGING_ANIMATION, true));
        disposable.add(getBooleanDisposable(CHARGING_ALARM, false));
        disposable.add(getBooleanDisposable(UNPLUGGED_ALARM, false));
        disposable.add(getBooleanDisposable(TOUCH_ALARM, false));
        disposable.add(getBooleanDisposable(ANTI_POCKET_ALARM, false));
        disposable.add(getBooleanDisposable(ANTI_THEFT_PROTECTION, false));
        disposable.add(getBooleanDisposable(SHOW_BATTERY_PERCENTAGE, true));
        disposable.add(getBooleanDisposable(SHOW_ON_LOCK_SCREEN, false));
        disposable.add(getStringDisposable(ALARM_CLOSING_PIN));
        disposable.add(getStringDisposable(SELECTED_ANIMATION_NAME));
        disposable.add(getIntegerDisposable(PLAY_DURATION, 0));
        disposable.add(getIntegerDisposable(CLOSE_METHOD, 1));

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
        linearAcceleration = new float[3];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            resetMediaPlayer();
            if (intent.getAction().equals(String.valueOf(Sensor.TYPE_ACCELEROMETER))) {
                dataStore.saveBooleanValue(TOUCH_ALARM, false);
            } else if (intent.getAction().equals(String.valueOf(Sensor.TYPE_PROXIMITY))) {
                dataStore.saveBooleanValue(ANTI_POCKET_ALARM, false);
            }
            synchronized (lock) {
                isAlarmShowing = false;
            }
        }
        Notification notification = createNotification();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        resetMediaPlayer();
        unregisterPowerConnectedReceiver();
        unregisterPowerDisconnectedReceiver();
        unregisterLockScreenReceiver();
        sensorManager.unregisterListener(this);
        releaseVibrator();
    }

    private void releaseVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (vibratorManager != null) {
                vibratorManager.cancel();
            }
        } else {
            if (vibrator != null) {
                vibrator.cancel();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content_text))
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentIntent(createPendingIntent());
        return builder.build();
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float acceleration = (float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            Log.d("MainActivity", "Device moved!" + event.values[0]+","+event.values[1]+","+event.values[2]);
            if (acceleration > THRESHOLD) {
                Log.d("MainActivity", "Device moved!" + acceleration);
                // Do something here when movement or displacement is detected
                if (!isAlarmShowing && !isPocket) {
                    showAlarmScreen(Sensor.TYPE_LINEAR_ACCELERATION);
                }

            }
        }*/
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

           /* final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linearAcceleration[0] = event.values[0] - gravity[0];
            linearAcceleration[1] = event.values[1] - gravity[1];
            linearAcceleration[2] = event.values[2] - gravity[2];

            float acceleration = (float) Math.sqrt(linearAcceleration[0] * linearAcceleration[0] + linearAcceleration[1] * linearAcceleration[1] + linearAcceleration[2] * linearAcceleration[2]);*/
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect

            //Log.d("MainActivity", "Device moved!" + mAccel);
            //Log.d("MainActivity", "Device moved!" + linearAcceleration[0]+","+linearAcceleration[1]+","+linearAcceleration[2]);
            if (mAccel > MOVEMENT_THRESHOLD) {
                synchronized (lock) {
                    if (!isAlarmShowing && !isPocket) {
                        Log.d("MainActivity", "Device moved!" + mAccel);
                        showAlarmScreen(Sensor.TYPE_ACCELEROMETER);
                        isAlarmShowing = true;
                    }
                }
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            //Log.d("TAG", "onSensorChanged: " + event.values[0] + proximity.getMaximumRange());
            synchronized (lock) {
                if (!isAlarmShowing && isPocket) {
                    showAlarmScreen(Sensor.TYPE_PROXIMITY);
                    isAlarmShowing = true;
                }
            }
            isPocket = event.values[0] < (proximity != null ? proximity.getMaximumRange() : 1);
            if (!isAlarmShowing && isPocket) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(300);
                }
            }
        }

        /*if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.d("TAG", "onSensorChanged: " + event.values[0] + shouldPlayAlarm + isPocket);
            if (event.values[0] < 5 && isPocket) {
                shouldPlayAlarm = true;
            } else {

            }
        }*/
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
        Log.d("TAG", "registerPowerDisconnectedReceiver: ");
        if (disconnectedReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            disconnectedReceiver = new PowerDisconnectedReceiver();
            registerReceiver(disconnectedReceiver, intentFilter);
        }
    }

    private void unregisterPowerDisconnectedReceiver() {
        Log.d("TAG", "unregisterPowerDisconnectedReceiver: ");
        try {
            unregisterReceiver(disconnectedReceiver);
            disconnectedReceiver = null;
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void unregisterLockScreenReceiver() {
        if (lockScreenReceiver != null) {
            try {
                unregisterReceiver(lockScreenReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    public void showAnimationScreen() {
        resetMediaPlayer();
        Intent animationIntent = new Intent(this, AnimationActivity.class);
        animationIntent.putExtra(Constants.KEY_IS_BATTERY_PERCENTAGE_ENABLED, isShowBatteryPercentageEnabled);
        animationIntent.putExtra(Constants.KEY_CLOSE_METHOD, closeMethod);
        animationIntent.putExtra(Constants.KEY_PLAY_DURATION, playDuration);
        animationIntent.putExtra(Constants.KEY_SELECTED_ANIMATION, selectedAnimation);
        animationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(animationIntent);
    }

    public void showAlarmScreen(int sensorType) {
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        alarmIntent.putExtra(Constants.KEY_ALARM_CLOSING_PIN, alarmClosingPing);
        alarmIntent.putExtra(Constants.KEY_TYPE, sensorType);
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

    public Disposable getBooleanDisposable(Preferences.Key<Boolean> key, Boolean
            defaultValue) {
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
                    registerPowerDisconnectedReceiver();
                } else {
                    if (!isChargingAnimationEnabled) unregisterPowerConnectedReceiver();
                    if (!isUnpluggedAlarmEnabled) unregisterPowerDisconnectedReceiver();
                }
            } else if (UNPLUGGED_ALARM.equals(key)) {
                isUnpluggedAlarmEnabled = value;
                if (value && isAntiTheftAlarmEnabled) {
                    registerPowerDisconnectedReceiver();
                } else if (!value && !isPluggedAlarmEnabled) {
                    unregisterPowerDisconnectedReceiver();
                }
            } else if (TOUCH_ALARM.equals(key)) {
                /*if(value) {
                    new Handler().postDelayed(() -> {
                        isTouchAlarmEnabled = true;
                        handleOnTouchAlarm();
                    }, 10000);
                }else {*/
                isTouchAlarmEnabled = value;
                handleOnTouchAlarm();
            } else if (ANTI_POCKET_ALARM.equals(key)) {
                isAntiPocketAlarmEnabled = value;
                handleAntiPocketAlarm();
            } else if (ANTI_THEFT_PROTECTION.equals(key)) {
                isAntiTheftAlarmEnabled = value;
                if (value && isUnpluggedAlarmEnabled) {
                    registerPowerDisconnectedReceiver();
                } else if (!value && !isPluggedAlarmEnabled) unregisterPowerDisconnectedReceiver();
                handleOnTouchAlarm();
                handleAntiPocketAlarm();
            } else if (SHOW_BATTERY_PERCENTAGE.equals(key)) {
                isShowBatteryPercentageEnabled = value;
            } else if (SHOW_ON_LOCK_SCREEN.equals(key)) {
                showAnimationOnLockScreen(value);
            }
        });
    }


    public Disposable getStringDisposable(Preferences.Key<String> key) {
        return dataStore.getStringValue(key).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(value -> {
            if (ALARM_CLOSING_PIN.equals(key)) {
                alarmClosingPing = value;
            } else if (SELECTED_ANIMATION_NAME.equals(key)) {
                selectedAnimation = value.isEmpty() ? Constants.DEFAULT_ANIMATION : value;
            }
        });
    }

    public Disposable getIntegerDisposable(Preferences.Key<Integer> key, int defaultValue) {
        return dataStore.getIntegerValue(key, defaultValue).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(value -> {
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
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (isAntiTheftAlarmEnabled && isTouchAlarmEnabled) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mAccel = 0.00f;
            mAccelCurrent = SensorManager.GRAVITY_EARTH;
            mAccelLast = SensorManager.GRAVITY_EARTH;
        } else {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
    }

    private void handleAntiPocketAlarm() {
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (isAntiTheftAlarmEnabled && isAntiPocketAlarmEnabled) {
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.unregisterListener(this, proximity);
        }
    }

    public void onChargerDisconnected() {
        if (isUnpluggedAlarmEnabled && isAntiTheftAlarmEnabled) {
            if (!isAlarmShowing) {
                showAlarmScreen(Constants.ALARM_TYPE_UNPLUGGED);
                isAlarmShowing = true;
            }
        } else if (isPluggedAlarmEnabled) {
            if (disconnectPreference == null) return;
            playSound(disconnectPreference);
        }
    }

    public void onChargerConnected() {
        if (!isAlarmShowing) {
            if (isChargingAnimationEnabled) {
                showAnimationScreen();
            }
            if (connectPreference == null) return;
            if (isPluggedAlarmEnabled) {
                playSound(connectPreference);
            }
        }
    }

    public void showAnimationOnLockScreen(){
        if (isChargingAnimationEnabled) {
            showAnimationScreen();
        }
    }

    public void playSound(PreferenceModel preferenceModel) {
        resetMediaPlayer();
        try {
            mediaPlayer = new MediaPlayer();
            if (Objects.equals(preferenceModel.getType(), FileType.ASSET)) {
                String fileName = getString(R.string.asset_folder_name) + "/" + preferenceModel.getName();
                Log.d("TAG", "onReceive: " + fileName);
                AssetFileDescriptor descriptor = getAssets().openFd(fileName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mediaPlayer.setDataSource(descriptor);
                } else {
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                }
                descriptor.close();
            } else {
                Log.d("TAG", "onChargerConnected: " + preferenceModel.getPath());
                mediaPlayer.setDataSource(this, Uri.parse(preferenceModel.getPath()));
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.d("TAG", "onReceive: " + e.getMessage());
        }
    }

    public void showAnimationOnLockScreen(boolean value) {
        if (value) {
            lockScreenReceiver = new LockScreenReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(lockScreenReceiver, intentFilter);
        } else {
            unregisterLockScreenReceiver();
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