package com.chargingstatusmonitor.souhadev.ui.sounds;

import static com.chargingstatusmonitor.souhadev.utils.FileUtils.getDuration;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.playRecording;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.showDialog;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.READ_PERMISSION_CODE;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.hasPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.utils.AppExecutors;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentMyRecordsBinding;
import com.chargingstatusmonitor.souhadev.model.FileModel;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.FileType;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecordsFragment extends Fragment implements MyRecordsAdapter.OnItemClickAction {
    private FragmentMyRecordsBinding binding;
    List<FileModel> records = new ArrayList<>();
    MyRecordsAdapter adapter;
    MediaPlayer mediaPlayer = new MediaPlayer();

    AppDataStore dataStore;


    public MyRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyRecordsBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        adapter = new MyRecordsAdapter(records, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO)) {
                getMyRecords(requireContext());
            } else {
                PermissionUtils.requestMediaPermission(this);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getMyRecords(requireContext());
            } else {
                PermissionUtils.requestReadPermission(this);
            }
        } else getMyRecords(requireContext());
        return binding.getRoot();
    }

    public void getMyRecords(Context context) {
        List<FileModel> recordingList = new ArrayList<>();
        AppExecutors.getInstance().networkIO().execute(() -> {
            Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) :
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
            };
            String selection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                    MediaStore.Audio.Media.RELATIVE_PATH + " LIKE ? " :
                    MediaStore.Audio.Media.DATA + " LIKE ? ";
            String[] selectionArgs = {
                    "%" + context.getString(R.string.recording_folder_name) + "%"
            };
            String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " DESC ";
            try (Cursor cursor = context.getContentResolver().query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            )) {
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(idColumn);
                        String name = cursor.getString(nameColumn);
                        String path = cursor.getString(pathColumn);
                        String duration = getDuration(context, path);
                        FileModel fileModel = new FileModel(id, name, duration, path);
                        recordingList.add(fileModel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                String folderName = context.getString(R.string.recording_folder_name);
                File folder = new File(Environment.getExternalStorageDirectory(), folderName);
                if (folder.exists() && folder.isDirectory()) {
                    File[] files = folder.listFiles((dir, name) ->
                            name.toLowerCase(Locale.getDefault()).endsWith(".mp3")
                    );
                    if (files != null) {
                        for (File file : files) {
                            String id = file.getName();
                            String name = file.getName();
                            String path = file.getAbsolutePath();
                            String duration = getDuration(context, path);
                            FileModel fileModel = new FileModel(id, name, duration, path);
                            if (find(recordingList, f -> f.getName().equals(fileModel.getName())) == null) {
                                recordingList.add(fileModel);
                            }
                        }
                    }
                }
            }
            Log.d("TAG", "getMyRecords: " + recordingList);
            AppExecutors.getInstance().mainThread().execute(() -> {
                records.clear();
                records.addAll(recordingList);
                adapter.notifyDataSetChanged();
            });
        });

    }

    public static <T> T find(List<T> list, Predicate<T> predicate) {
        for (T element : list) {
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyRecords(requireContext());
                } else {
                    Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        if (adapter.getSelectedItem() == position) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                adapter.setSelectedItem(-1);
                return;
            }
        }
        adapter.setSelectedItem(position);
        playRecording(requireContext(), mediaPlayer, records.get(position).getPath(), () -> adapter.setSelectedItem(-1));
    }

    @Override
    public void onSetRingtoneClick(int position) {
        FileModel file = records.get(position);
        showDialog(requireContext(), dataStore, file.getName(), FileType.RECORD + Constants.SPLITTER + file.getPath());
    }
}