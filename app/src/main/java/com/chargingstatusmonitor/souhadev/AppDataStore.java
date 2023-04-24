package com.chargingstatusmonitor.souhadev;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

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

    Preferences.Key<String> CONNECT_FILE_KEY = PreferencesKeys.stringKey("connect_file");
    Preferences.Key<String> DISCONNECT_FILE_KEY = PreferencesKeys.stringKey("disconnect_file");

    public Flowable<String> getOnConnectFile() {
        return dataStore.data()
                .map(preferences -> preferences.get(CONNECT_FILE_KEY) == null ? FileType.ASSET + "-asset-" + context.getResources().getString(R.string.mp3_country) + ".mp3" : preferences.get(CONNECT_FILE_KEY));
    }

    public Flowable<String> getOnDisconnectFile() {
        return dataStore.data()
                .map(preferences -> preferences.get(DISCONNECT_FILE_KEY) == null ? FileType.ASSET + "-asset-" + context.getResources().getString(R.string.mp3_disconnect_country) + ".mp3" : preferences.get(DISCONNECT_FILE_KEY));
    }


    public void saveOnConnectFile(String value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(CONNECT_FILE_KEY, value);
            return Single.just(mutablePreferences);
        });
    }

    public void saveOnDisconnectFile(String value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(DISCONNECT_FILE_KEY, value);
            return Single.just(mutablePreferences);
        });
    }
}
