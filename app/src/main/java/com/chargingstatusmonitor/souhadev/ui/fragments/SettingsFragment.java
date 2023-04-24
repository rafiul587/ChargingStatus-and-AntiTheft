package com.chargingstatusmonitor.souhadev.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chargingstatusmonitor.souhadev.BuildConfig;
import com.chargingstatusmonitor.souhadev.Continue;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.FragmentSettingsBinding;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public SettingsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        ReviewManager reviewManager = ReviewManagerFactory.create(requireContext());

        binding.versionNo.setText(BuildConfig.VERSION_NAME);

        binding.rateUs.setOnClickListener(v -> {
            Continue.count_rate++;
            if (Continue.count_rate > 1) {
                rateApp(requireContext());
            } else {
                reviewManager.requestReviewFlow().addOnCompleteListener(it -> {
                    if (it.isSuccessful()) {
                        reviewManager.launchReviewFlow(requireActivity(), it.getResult());
                    }
                });
            }
        });
        binding.share.setOnClickListener(v -> {
            shareApp(requireContext());
        });
        binding.moreApps.setOnClickListener(v -> {
            showMoreApps(requireContext());
        });
        return binding.getRoot();
    }


    private void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        String shareMessage = "\nLet me recommend you this application\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "Choose one"));
    }

    private void showMoreApps(Context context) {
        //String str = "https://play.google.com/store/apps/developer?id=YOUR_DEVELOPER_ID";
        String str = "https://play.google.com/store/apps/developer?id=Souha+Dev";
        //https://play.google.com/store/apps/developer?id=Souha+Dev
        //String str = "https://play.google.com/store/apps/dev?id=Gt0";
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    public void rateApp(Context context) {
        String urlStrRateUs;
        urlStrRateUs = "market://details?id=" + context.getPackageName();
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}