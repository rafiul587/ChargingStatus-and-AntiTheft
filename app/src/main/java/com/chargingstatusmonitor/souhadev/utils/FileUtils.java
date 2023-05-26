package com.chargingstatusmonitor.souhadev.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FileUtils {

    public static void startDownload(Context context, String fileName) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            File directoryTest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.download_folder_name));
            if (!directoryTest.exists()) {
                directoryTest.mkdirs();
            }
            try {
                InputStream in = context.getAssets().open(context.getString(R.string.asset_folder_name) + "/" + fileName);
                File file = new File(directoryTest.getPath() + "/" + fileName);
                if (file.exists()) {
                    file.delete();
                }
                OutputStream out = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int read;
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppExecutors.getInstance().mainThread().execute(() -> {
                Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
            });
        });
    }

    public static void playFile(Context context, MediaPlayer mediaPlayer, String fileName, Runnable onFinish) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                AssetFileDescriptor descriptor = context.getAssets().openFd(context.getString(R.string.asset_folder_name) + "/" + fileName);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnCompletionListener(mp -> {
                    onFinish.run();
                });
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void showDialog(Context context, AppDataStore dataStore, String fileName, String fileType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.dialog_view, null);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        Button onConnectButton = view.findViewById(R.id.onConnectButton);
        Button onDisconnectButton = view.findViewById(R.id.onDisconnectButton);
        Spanned message = Html.fromHtml(context.getResources().getString(R.string.set) + " <font><b> " + fileName + " </b></font> " + context.getResources().getString(R.string.as_the_ringtone));
        messageTextView.setText(message);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        onConnectButton.setOnClickListener(v -> {
            AppExecutors.getInstance().networkIO().execute(() -> {
                // perform IO operation on a separate thread
                dataStore.saveStringValue(AppDataStore.CONNECT_FILE_KEY, fileType + Constants.SPLITTER + fileName);
                new Handler(Looper.getMainLooper()).post(dialog::dismiss);
            });
        });

        onDisconnectButton.setOnClickListener(v -> {
            AppExecutors.getInstance().networkIO().execute(() -> {
                // perform IO operation on a separate thread
                dataStore.saveStringValue(AppDataStore.DISCONNECT_FILE_KEY, fileType + Constants.SPLITTER + fileName);
                new Handler(Looper.getMainLooper()).post(dialog::dismiss);
            });
        });

        dialog.show();
    }

    public static void playRecording(Context context, MediaPlayer mediaPlayer, String path, Runnable onFinish) {
        if(mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(path));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(mp -> onFinish.run());
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("EACCES")) {
                Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.falied_to_play), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getDuration(Context context, String filePath) {

        long duration = 0L;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(filePath));
            duration = Long.parseLong(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
            ));
            retriever.release();
        } catch (Exception e) {
            Log.d("TAG", "getDuration: " + e.getMessage());
        }
        return new SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration);
    }

    public static String getDuration(long duration) {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
