package com.chargingstatusmonitor.souhadev.ui.fragments;

import static com.chargingstatusmonitor.souhadev.utils.PermissionUtils.RECORD_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentRecordBinding;
import com.chargingstatusmonitor.souhadev.utils.PermissionUtils;
import com.google.android.material.tabs.TabLayout;

public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;
    private VoiceRecordFragment voiceRecordFragment;
    private MyRecordsFragment myRecordsFragment;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);

        voiceRecordFragment = new VoiceRecordFragment();
        myRecordsFragment = new MyRecordsFragment();
        replaceFragment(voiceRecordFragment);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Voice Recorder"), true);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My Records"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return binding.getRoot();
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(voiceRecordFragment);
                break;
            case 1:
                replaceFragment(myRecordsFragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true).replace(R.id.fragmentContainerView, fragment).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}