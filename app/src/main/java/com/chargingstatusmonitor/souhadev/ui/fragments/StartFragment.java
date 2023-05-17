package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.IS_FIRST_TIME_INSTALL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.ui.activities.MainActivity;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentStartBinding;
import com.chargingstatusmonitor.souhadev.ui.guideline.GuideScreenActivity;

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