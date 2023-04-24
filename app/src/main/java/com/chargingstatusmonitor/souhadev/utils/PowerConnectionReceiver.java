package com.chargingstatusmonitor.souhadev.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.model.PreferenceModel;

import java.io.IOException;
import java.util.Objects;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private final PreferenceModel connectModel;
    private final PreferenceModel disconnectModel;

    public PowerConnectionReceiver(PreferenceModel connectModel, PreferenceModel disconnectModel) {
        this.connectModel = connectModel;
        this.disconnectModel = disconnectModel;
    }

    @Override
    public void onReceive(Context context, Intent batteryStatus) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        switch (batteryStatus.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                try {
                    mediaPlayer = new MediaPlayer();
                    if (Objects.equals(connectModel.getType(), FileType.ASSET)) {
                        String fileName = context.getString(R.string.asset_folder_name) + "/" + connectModel.getName();
                        Log.d("TAG", "onReceive: " + fileName);
                        AssetFileDescriptor descriptor = context.getAssets().openFd(fileName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mediaPlayer.setDataSource(descriptor);
                        } else {
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        }
                        descriptor.close();
                    } else {
                        mediaPlayer.setDataSource(context, Uri.parse(connectModel.getPath()));
                    }
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Log.d("TAG", "onReceive: " + e.getMessage());
                }
                break;

            case Intent.ACTION_POWER_DISCONNECTED:
                try {
                    mediaPlayer = new MediaPlayer();
                    if (Objects.equals(disconnectModel.getType(), FileType.ASSET)) {
                        String fileName = context.getString(R.string.asset_folder_name) + "/" + disconnectModel.getName();

                        AssetFileDescriptor descriptor = context.getAssets().openFd(fileName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mediaPlayer.setDataSource(descriptor);
                        } else {
                            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                        }
                        descriptor.close();
                    } else {
                        mediaPlayer.setDataSource(context, Uri.parse(disconnectModel.getPath()));
                    }
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Log.d("TAG", "onReceive: " + e.getMessage());
                }
                break;
        }
    }
}
