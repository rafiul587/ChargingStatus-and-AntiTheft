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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.utils.AppExecutors;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.local.FileDao;
import com.chargingstatusmonitor.souhadev.data.local.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.FragmentPhoneBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.FileType;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends Fragment implements RingtonesAdapter.OnItemClickAction {
    private FragmentPhoneBinding binding;
    RingtonesAdapter adapter;
    List<FileEntity> fileModels = new ArrayList<>();
    FileDao dao;
    AppDataStore dataStore;
    NavController navController;
    MediaPlayer mediaPlayer;

    public PhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhoneBinding.inflate(inflater, container, false);
        mediaPlayer = new MediaPlayer();
        dao = ((MyApplication) requireContext().getApplicationContext()).getDao();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        navController = NavHostFragment.findNavController(this);
        adapter = new RingtonesAdapter(fileModels, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        dao.getAllRingtones().observe(getViewLifecycleOwner(), fileEntities -> {
            fileModels.clear();
            fileModels.addAll(fileEntities);
            adapter.notifyDataSetChanged();
        });
        getRingtonesFromPhone(requireContext(), dao);
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
        showDialog(requireContext(), dataStore, fileEntity.getName(), FileType.RINGTONE + Constants.SPLITTER + fileEntity.getUri());
    }

    @Override
    public void onDownloadClick(int position) {

    }

    @Override
    public void onItemClick(int position) {
        FileEntity fileEntity = fileModels.get(position);
        if (fileEntity.isLocked()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_ID, fileEntity.getUid());

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
            playRecording(requireContext(), mediaPlayer, fileEntity.getUri(), () -> adapter.setSelectedItem(-1));
        }
    }

    public void getRingtonesFromPhone(Context context, FileDao dao) {
        binding.loadingText.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        AppExecutors.getInstance().networkIO().execute(() -> {
            int rowCount = dao.getRingtoneRowCount();
            if (rowCount <= 0) {
                RingtoneManager manager = new RingtoneManager(context);
                manager.setType(RingtoneManager.TYPE_RINGTONE);
                Cursor cursor = manager.getCursor();
                List<FileEntity> list = new ArrayList<>();
                int counter = 0;
                try {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    while (cursor.moveToNext()) {
                        counter++;
                        String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                        String ringtoneUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
                        try {
                            mmr.setDataSource(context, Uri.parse(ringtoneUri));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        list.add(new FileEntity(
                                ringtoneTitle,
                                FileType.RINGTONE,
                                ringtoneUri,
                                getDuration(duration != null ? Long.parseLong(duration) : 0),
                                counter > cursor.getCount() / 2));
                    }

                    mmr.release();
                    dao.insertAll(list);
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        Log.d("TAG", "getRingtonesFromPhone: "+ list.toString());
                        if (list.isEmpty()) {
                            binding.loadingText.setVisibility(View.VISIBLE);
                            binding.loadingText.setText(getString(R.string.no_ringtones));
                        } else binding.loadingText.setVisibility(View.GONE);

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        binding.loadingText.setVisibility(View.VISIBLE);
                        binding.loadingText.setText(getString(R.string.something_wrong));

                    });
                }

            }else {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    binding.loadingText.setVisibility(View.GONE);
                });
            }
        });
        binding.progressBar.setVisibility(View.GONE);
    }
}