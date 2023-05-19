package com.chargingstatusmonitor.souhadev.ui.charginganimation;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentApplyAnimationBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;

import java.io.File;
import java.util.Arrays;

public class ApplyAnimationFragment extends Fragment {

    private FragmentApplyAnimationBinding binding;
    AppDataStore dataStore;
    String animationName;

    public ApplyAnimationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animationName = getArguments().getString(Constants.KEY_NAME, Constants.DEFAULT_ANIMATION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentApplyAnimationBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        if (animationName.equals(Constants.DEFAULT_ANIMATION)) {
            Glide.with(this)
                    .asGif()
                    .load("file:///android_asset/animations/"+ Constants.DEFAULT_ANIMATION)
                    .into(binding.animation);
        } else {
            File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), requireContext().getString(R.string.download_folder_name));
            File[] files = storagePath.listFiles((dir, name) -> name.equals(animationName));
            Log.d("TAG", "onCreateView: " + Arrays.toString(files));
            if (files != null && files.length > 0) {
                Glide.with(this)
                        .asGif()
                        .load(files[0])
                        .into(binding.animation);
            }
        }

        binding.apply.setOnClickListener(v -> {
            dataStore.saveStringValue(AppDataStore.SELECTED_ANIMATION_NAME, animationName);
            Toast.makeText(requireContext(), "The animation has been added successfully!", Toast.LENGTH_SHORT).show();
        });
        binding.settings.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_apply_animation_to_navigation_animation_settings);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}