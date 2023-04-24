package com.chargingstatusmonitor.souhadev.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentRingtoneBinding;

public class RingtoneFragment extends Fragment {

    private FragmentRingtoneBinding binding;

    NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRingtoneBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        binding.phone.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_ringtone_to_navigation_phone);
        });
        binding.download.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_ringtone_to_navigation_download);
        });
        binding.record.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_ringtone_to_navigation_record);
        });
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}