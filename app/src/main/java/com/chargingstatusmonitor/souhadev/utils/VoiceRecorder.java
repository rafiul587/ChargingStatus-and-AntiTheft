package com.chargingstatusmonitor.souhadev.utils;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.chargingstatusmonitor.souhadev.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoiceRecorder {
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private final Context context;

    public VoiceRecorder(Context context) {
        this.context = context;
    }

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            String directory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                directory = Environment.DIRECTORY_MUSIC + "/" + context.getString(R.string.recording_folder_name);
            } else {
                directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.recording_folder_name);
            }

            String fileName = getFileName();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, fileName);
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, directory);
            } else {
                File folder = new File(directory);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(folder, fileName + ".mp3");
                values.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
            }

            Uri audioUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            ParcelFileDescriptor descriptor = context.getContentResolver().openFileDescriptor(audioUri, "w");

            if (descriptor != null) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setOutputFile(descriptor.getFileDescriptor());
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    Log.e("startRecording", e.getMessage());
                }
                recorder.start();
                descriptor.close();
            }
        } catch (Exception e) {
            Log.d("TAG", "startRecording: "+ e.getMessage());
            FirebaseCrashlytics.getInstance().log(e.getMessage() != null ? e.getMessage() : "");
        }
    }

    private String getFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String fileName = formatter.format(new Date());
        return context.getString(R.string.record_file_prefix) + fileName;
    }

    private void stopRecording() {
        if (recorder != null) {
            if (isRecording) {
                recorder.stop();
            }
            recorder.release();
        }
        recorder = null;
        isRecording = false;
    }
}