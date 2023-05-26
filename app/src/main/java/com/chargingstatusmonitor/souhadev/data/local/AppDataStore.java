package com.chargingstatusmonitor.souhadev.data.local;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.FileType;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class AppDataStore {
    private static final String PREFERENCES_NAME = "preferences";
    RxDataStore<Preferences> dataStore;
    Context context;


    public AppDataStore(Context context) {
        this.context = context;
        dataStore = new RxPreferenceDataStoreBuilder(context, PREFERENCES_NAME).build();
    }

    public static Preferences.Key<String> CONNECT_FILE_KEY = PreferencesKeys.stringKey("connect_file");
    public static Preferences.Key<String> DISCONNECT_FILE_KEY = PreferencesKeys.stringKey("disconnect_file");
    public static Preferences.Key<Boolean> CHARGING_ANIMATION = PreferencesKeys.booleanKey("charging_animation");
    public static Preferences.Key<Boolean> ANTI_THEFT_PROTECTION = PreferencesKeys.booleanKey("anti_theft_protection");
    public static Preferences.Key<Boolean> CHARGING_ALARM = PreferencesKeys.booleanKey("charging_alarm");
    public static Preferences.Key<Boolean> UNPLUGGED_ALARM = PreferencesKeys.booleanKey("unplugged_alarm");
    public static Preferences.Key<Boolean> TOUCH_ALARM = PreferencesKeys.booleanKey("touch_alarm");
    public static Preferences.Key<Boolean> ANTI_POCKET_ALARM = PreferencesKeys.booleanKey("anti_pocket_alarm");
    public static Preferences.Key<Integer> PLAY_DURATION = PreferencesKeys.intKey("play_duration");
    public static Preferences.Key<Integer> CLOSE_METHOD = PreferencesKeys.intKey("close_method");
    public static Preferences.Key<Boolean> SHOW_BATTERY_PERCENTAGE = PreferencesKeys.booleanKey("show_battery_percentage");
    public static Preferences.Key<Boolean> SHOW_ON_LOCK_SCREEN = PreferencesKeys.booleanKey("show_on_lock_screen");
    public static Preferences.Key<Boolean> IS_FIRST_TIME_INSTALL = PreferencesKeys.booleanKey("is_first_time");
    public static Preferences.Key<String> ALARM_CLOSING_PIN = PreferencesKeys.stringKey("alarm_closing_pin");
    public static Preferences.Key<String> SELECTED_ANIMATION_NAME = PreferencesKeys.stringKey("selected_animation_name");

    public Flowable<String> getOnConnectFile() {
        return dataStore.data()
                .map(preferences -> preferences.get(CONNECT_FILE_KEY) == null ? FileType.ASSET + Constants.SPLITTER +"asset"+Constants.SPLITTER + context.getResources().getString(R.string.mp3_country) + ".mp3" : preferences.get(CONNECT_FILE_KEY));
    }

    public Flowable<String> getOnDisconnectFile() {
        return dataStore.data()
                .map(preferences -> preferences.get(DISCONNECT_FILE_KEY) == null ? FileType.ASSET +  Constants.SPLITTER +"asset"+Constants.SPLITTER + context.getResources().getString(R.string.mp3_disconnect_country) + ".mp3" : preferences.get(DISCONNECT_FILE_KEY));
    }

    public Flowable<Boolean> getBoolean(Preferences.Key<Boolean> key, Boolean defaultValue) {
        if (defaultValue) return dataStore.data().map(preferences -> preferences.get(key) == null || preferences.get(key));
        else return dataStore.data().map(preferences -> preferences.get(key) != null && preferences.get(key));
    }

    public Flowable<String> getStringValue(Preferences.Key<String> key) {
        return dataStore.data()
                .map(preferences -> preferences.get(key) == null ? "" : preferences.get(key));
    }

    public Flowable<Integer> getIntegerValue(Preferences.Key<Integer> key, int defaultValue) {
        return dataStore.data()
                .map(preferences -> preferences.get(key) == null ? defaultValue : preferences.get(key));
    }

    public void saveIntegerValue(Preferences.Key<Integer> key, Integer value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }

    public void saveBooleanValue(Preferences.Key<Boolean> key, Boolean value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }

    public void saveStringValue(Preferences.Key<String> key, String value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }
}
