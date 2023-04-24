package com.chargingstatusmonitor.souhadev.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentLoadingBinding;

public class LoadingFragment extends Fragment {

    private FragmentLoadingBinding binding;

    private int progressStatus = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private boolean isNavigated = false;


    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        runnable = new Runnable() {
            @Override
            public void run() {
                progressStatus += 10;
                binding.progress.setText(progressStatus+"%");
                if (progressStatus == 100) {
                    // Wait for 500ms and then navigate to the next screen
                    if (!isNavigated) {
                        isNavigated = true;
                        handler.postDelayed(() -> requireActivity().getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerView, new StartFragment())
                                .commitAllowingStateLoss(), 500);
                    }
                } else {
                    handler.postDelayed(this, 200);
                }
            }
        };

        handler.postDelayed(runnable, 200);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }
}