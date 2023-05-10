package com.chargingstatusmonitor.souhadev;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.chargingstatusmonitor.souhadev.databinding.ActivitySearchBinding;
import com.chargingstatusmonitor.souhadev.databinding.ActivitySettingsBinding;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ReviewManager reviewManager = ReviewManagerFactory.create(this);

        binding.versionNo.setText(BuildConfig.VERSION_NAME);

        binding.rateUs.setOnClickListener(v -> {
            Continue.count_rate++;
            if (Continue.count_rate > 1) {
                rateApp(this);
            } else {
                reviewManager.requestReviewFlow().addOnCompleteListener(it -> {
                    if (it.isSuccessful()) {
                        reviewManager.launchReviewFlow(this, it.getResult());
                    }
                });
            }
        });
        binding.share.setOnClickListener(v -> {
            shareApp(this);
        });
        binding.moreApps.setOnClickListener(v -> {
            showMoreApps(this);
        });
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
}