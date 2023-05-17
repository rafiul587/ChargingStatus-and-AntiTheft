package com.chargingstatusmonitor.souhadev.ui.sounds;

import static com.chargingstatusmonitor.souhadev.utils.FileUtils.playFile;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.playRecording;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.showDialog;
import static com.chargingstatusmonitor.souhadev.utils.FileUtils.startDownload;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chargingstatusmonitor.souhadev.utils.AppExecutors;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.local.FileDao;
import com.chargingstatusmonitor.souhadev.data.local.FileEntity;
import com.chargingstatusmonitor.souhadev.databinding.FragmentSearchBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;
import com.chargingstatusmonitor.souhadev.utils.FileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements RingtonesAdapter.OnItemClickAction {

    RingtonesAdapter adapter;
    List<FileEntity> fileModels = new ArrayList<>();

    FileDao dao;
    AppDataStore dataStore;
    MediaPlayer mediaPlayer;

    private FragmentSearchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        mediaPlayer = new MediaPlayer();
        dao = ((MyApplication) requireContext().getApplicationContext()).getDao();
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        adapter = new RingtonesAdapter(fileModels, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppExecutors.getInstance().networkIO().execute(() -> {
                    List<FileEntity> list = dao.findByName("%" + s.toString() + "%");
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        if (binding.searchBox.getText().length() > 0) {
                            if (list.isEmpty()) {
                                binding.emptyLayout.setVisibility(View.VISIBLE);
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.searchText.setText(getString(R.string.no_ringtones));
                            } else {
                                binding.emptyLayout.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);
                            }
                            fileModels.clear();
                            fileModels.addAll(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.emptyLayout.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.searchText.setText(getString(R.string.text_search_ringtones));
                        }
                    });
                });
            }
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
        String type = Objects.equals(fileEntity.getType(), FileType.ASSET)
                ? FileType.ASSET + "-" + fileEntity.getUri()
                : FileType.RINGTONE + "-" + fileEntity.getUri();
        showDialog(requireContext(), dataStore, fileEntity.getName(), type);
    }

    @Override
    public void onDownloadClick(int position) {
        startDownload(requireContext(), fileModels.get(position).getName());
    }

    @Override
    public void onItemClick(int position) {
        FileEntity fileEntity = fileModels.get(position);
        if (fileEntity.isLocked()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_ID, fileEntity.getUid());
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_search_to_navigation_unlock, bundle);
        } else {
            if (adapter.getSelectedItem() == position) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                return;
            }
            adapter.setSelectedItem(position);
            if (fileEntity.getType().equals(FileType.ASSET)) {
                playFile(requireContext(), mediaPlayer, fileEntity.getName(), () -> adapter.setSelectedItem(-1));
            } else {
                playRecording(requireContext(), mediaPlayer, fileEntity.getUri(), () -> adapter.setSelectedItem(-1));
            }
        }
    }
}