package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.utils.FileUtils.getDuration;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.playFile;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.showDialog;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.startDownload;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.WRITE_PERMISSION_CODE;
import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.hasPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.RingtonesAdapter;
import com.chargingstatusmonitor.souhadev.data.FileDao;
import com.chargingstatusmonitor.souhadev.data.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.FragmentDownloadBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.FileType;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadFragment extends Fragment implements RingtonesAdapter.OnItemClickAction {
    private FragmentDownloadBinding binding;
    RingtonesAdapter adapter;
    NavController navController;
    List<FileEntity> fileModels = new ArrayList<>();
    FileDao dao;
    AppDataStore dataStore;
    MediaPlayer mediaPlayer;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        mediaPlayer = new MediaPlayer();
        dao = ((MyApplication) requireContext().getApplicationContext()).getDao();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        adapter = new RingtonesAdapter(fileModels, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        getAllFilesFromAssets(dao, requireContext());
        dao.getAllAssetsFiles().observe(getViewLifecycleOwner(), fileEntities -> {
            fileModels.clear();
            fileModels.addAll(fileEntities);
            adapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            adapter.setSelectedItem(-1);
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.release();
        binding = null;
    }

    @Override
    public void onSetRingtoneClick(int position) {
        FileEntity fileEntity = fileModels.get(position);
        showDialog(requireContext(), dataStore, fileEntity.getName(), FileType.ASSET + Constants.SPLITTER + fileEntity.getUri());
    }

    @Override
    public void onDownloadClick(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (hasPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startDownload(requireContext(), fileModels.get(position).getName());
            } else {
                PermissionUtils.requestWritePermission(this);
            }
        }else startDownload(requireContext(), fileModels.get(position).getName());
    }

    @Override
    public void onItemClick(int position) {
        FileEntity fileEntity = fileModels.get(position);
        if (fileEntity.isLocked()) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", fileEntity.getUid());
            navController.navigate(R.id.action_to_navigation_unlock, bundle);
        } else {
            if (adapter.getSelectedItem() == position) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    adapter.setSelectedItem(-1);
                    return;
                }
            }
            adapter.setSelectedItem(position);
            playFile(requireContext(), mediaPlayer, fileEntity.getName(), () -> adapter.setSelectedItem(-1));
        }
    }

    public void getAllFilesFromAssets(FileDao dao, Context context) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            int rowCount = dao.getAssetRowCount();
            if (rowCount <= 0) {
                List<FileEntity> list = new ArrayList<>();
                try {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

                    for (String item : context.getResources().getAssets().list(context.getString(R.string.asset_folder_name))) {
                        AssetFileDescriptor d = context.getAssets().openFd(context.getString(R.string.asset_folder_name) + "/" + item);
                        mmr.setDataSource(d.getFileDescriptor(), d.getStartOffset(), d.getLength());
                        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        list.add(new FileEntity(
                                item,
                                FileType.ASSET,
                                "asset",
                                getDuration(duration != null ? Long.parseLong(duration) : 0),
                                list.size() > 0
                        ));
                        d.close();
                    }
                    mmr.release();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                dao.insertAll(list);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}