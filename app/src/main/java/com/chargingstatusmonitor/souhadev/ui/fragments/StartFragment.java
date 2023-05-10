package com.chargingstatusmonitor.souhadev.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.chargingstatusmonitor.souhadev.AppDataStore.IS_FIRST_TIME_INSTALL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentLoadingBinding;
import com.chargingstatusmonitor.souhadev.databinding.FragmentStartBinding;
import com.chargingstatusmonitor.souhadev.utils.Constants;

public class StartFragment extends Fragment {

    private FragmentStartBinding binding;
    AppDataStore dataStore;
    private boolean firstTime;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStartBinding.inflate(inflater, container, false);
        String isFirstTimeInstall = "isFirstTimeInstall";
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        firstTime = dataStore.getBoolean(IS_FIRST_TIME_INSTALL, true).blockingFirst();
        binding.startButton.setOnClickListener(v -> {
            if (firstTime) {
                dataStore.saveBooleanValue(IS_FIRST_TIME_INSTALL, false);
                requireActivity().startActivity(new Intent(requireContext(), GuideScreenActivity.class));
                requireActivity().finish();
            }else {
                requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}