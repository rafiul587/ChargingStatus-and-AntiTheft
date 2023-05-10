package com.chargingstatusmonitor.souhadev.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.chargingstatusmonitor.souhadev.AppExecutors;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.data.FileDao;
import com.chargingstatusmonitor.souhadev.databinding.FragmentUnlockBinding;

import java.util.Objects;

public class UnlockFragment extends Fragment {
    private FragmentUnlockBinding binding;

    private FileDao dao;
    private int id;
    public UnlockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUnlockBinding.inflate(inflater, container, false);
        dao = ((MyApplication) requireContext().getApplicationContext()).getDao();
        binding.unlock.setOnClickListener(v -> {
            AppExecutors.getInstance().networkIO().execute(() -> {
                int count = dao.update(id);
                if (count > 0) {
                    AppExecutors.getInstance().mainThread().execute(() -> {
                        binding.text.setText(getString(R.string.success));
                        binding.unlock.setVisibility(View.GONE);
                        binding.ok.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
        binding.ok.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}