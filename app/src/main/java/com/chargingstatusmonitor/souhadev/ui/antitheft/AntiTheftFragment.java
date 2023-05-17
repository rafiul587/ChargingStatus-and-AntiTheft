package com.chargingstatusmonitor.souhadev.ui.antitheft;

import static com.chargingstatusmonitor.souhadev.data.local.AppDataStore.ALARM_CLOSING_PIN;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentAntiTheftBinding;

public class AntiTheftFragment extends Fragment {

    private FragmentAntiTheftBinding binding;
    NavController navController;
    AppDataStore dataStore;

    public AntiTheftFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAntiTheftBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        navController = NavHostFragment.findNavController(this);

        if(dataStore.getStringValue(ALARM_CLOSING_PIN).blockingFirst().isEmpty()){
            navController.navigate(R.id.action_navigation_anti_theft_to_navigation_set_pin);
        }
        binding.chargingAlarmCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_anti_theft_to_navigation_charging_alarm);
        });
        binding.touchAlarmCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_anti_theft_to_navigation_touch_alarm);
        });
        binding.pocketAlarmCard.setOnClickListener(v -> {
            navController.navigate(R.id.action_navigation_anti_theft_to_navigation_anti_pocket_alarm);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}