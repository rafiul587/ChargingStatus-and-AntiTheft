package com.chargingstatusmonitor.souhadev.ui.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentHomeBinding;
import com.chargingstatusmonitor.souhadev.utils.MyService;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    AppDataStore dataStore;
    private CompositeDisposable disposable;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        requireActivity().setTitle(getString(R.string.home_screen_title));
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        disposable = new CompositeDisposable();
        if(isMyServiceRunning(MyService.class)) {
            binding.startService.setText(requireContext().getString(R.string.stop_service_btn_text));
        } else {
            binding.startService.setText(requireContext().getString(R.string.start_service_btn_text));
        }
        binding.startService.setOnClickListener(v -> {
            if (isMyServiceRunning(MyService.class)) {
                requireContext().stopService(new Intent(requireContext(), MyService.class));
                binding.startService.setText(requireContext().getString(R.string.start_service_btn_text));
            } else {
                requireContext().startService(new Intent(requireContext(), MyService.class));
                binding.startService.setText(requireContext().getString(R.string.stop_service_btn_text));
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disposable.add(dataStore.getOnConnectFile()
                .subscribeOn(Schedulers.io()) // Emit values on an I/O thread// Receive values on the main thread
                .subscribe(connectFile -> {
                    // Handle each emitted value here on the main thread
                    handler.post(() -> {
                        binding.connectFile.setText(connectFile.split("-")[2]);
                    });
                }));
        disposable.add(dataStore.getOnDisconnectFile()
                .subscribeOn(Schedulers.io()) // Emit values on an I/O thread// Receive values on the main thread
                .subscribe(disconnectFile -> {
                    // Handle each emitted value here on the main thread
                    handler.post(() -> {
                        binding.disconnectFile.setText(disconnectFile.split("-")[2]);
                    });
                }));

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.dispose();
        binding = null;
    }
}