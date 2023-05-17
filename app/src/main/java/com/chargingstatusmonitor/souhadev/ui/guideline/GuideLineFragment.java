package com.chargingstatusmonitor.souhadev.ui.guideline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chargingstatusmonitor.souhadev.databinding.FragmentGuidelineBinding;
import com.chargingstatusmonitor.souhadev.model.GuideLine;

public class GuideLineFragment extends Fragment {

    private FragmentGuidelineBinding binding;

    GuideLine guideLine;

    public GuideLineFragment(GuideLine guideLine) {
        this.guideLine = guideLine;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGuidelineBinding.inflate(inflater, container, false);
        binding.image.setImageResource(guideLine.getImageResId());
        binding.description.setText(getString(guideLine.getDescription()));
        binding.title.setText(guideLine.getTitle());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}