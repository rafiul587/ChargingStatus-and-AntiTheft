package com.chargingstatusmonitor.souhadev.utils;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.model.PreferenceModel;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyService extends Service {
    private PowerConnectionReceiver mReceiver;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isRegistered = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Flowable<String> onConnectFileObservable = ((MyApplication) getApplicationContext())
                .getDataStore().getOnConnectFile().subscribeOn(Schedulers.io());

        Flowable<String> onDisconnectFileObservable = ((MyApplication) getApplicationContext())
                .getDataStore().getOnDisconnectFile().subscribeOn(Schedulers.io());

        Flowable<Pair<PreferenceModel, PreferenceModel>> observable = Flowable.zip(onConnectFileObservable,
                onDisconnectFileObservable,
                (connect, disconnect) -> {
                    String[] connectSplits = connect.split("-");
                    String connectType = connectSplits[0];
                    String path = connectSplits[1];
                    String connectFile = connectSplits[2];
                    PreferenceModel connectFileModel = new PreferenceModel(connectFile, connectType, path);

                    String[] disconnectSplits = disconnect.split("-");
                    String disconnectType = disconnectSplits[0];
                    String disconnectPath = disconnectSplits[1];
                    String disconnectFile = disconnectSplits[2];
                    PreferenceModel disconnectFileModel =
                            new PreferenceModel(disconnectFile, disconnectType, disconnectPath);

                    return new Pair<>(connectFileModel, disconnectFileModel);
                });

        Disposable disposable = observable
                .subscribe(pair -> {
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        Log.d("TAG", "onStartCommand: " + pair.first.getName() + ", " + pair.second.getName());
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
                        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

                        try {
                            // Register or UnRegister your broadcast receiver here
                            if (isRegistered) {
                                unregisterReceiver(mReceiver);
                            }
                            mReceiver = new PowerConnectionReceiver(pair.first, pair.second);
                            registerReceiver(mReceiver, intentFilter);
                            isRegistered = true;
                        } catch (IllegalArgumentException e) {
                            FirebaseCrashlytics.getInstance().log(e.toString() );
                        }
                    });
                }, throwable -> {
                    FirebaseCrashlytics.getInstance().log(throwable.toString());
                });


        compositeDisposable.add(disposable);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (mReceiver != null && isRegistered) {
            unregisterReceiver(mReceiver);
            isRegistered = false;
        }
        super.onDestroy();
    }
}