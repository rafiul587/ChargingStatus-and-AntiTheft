package com.chargingstatusmonitor.souhadev.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.snake.MainActivity_Snake;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class Continue extends Activity {
    TextView btnContinue;
    TextView btnShare;
    TextView btnExit;
    TextView btn_play;
    TextView btn_rate;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_continue);
        this.btnContinue = (TextView) findViewById(R.id.btn_continue);
        this.btn_play = (TextView) findViewById(R.id.btn_play);
        this.btn_rate = (TextView) findViewById(R.id.btn_rate);
        this.btnExit = (TextView) findViewById(R.id.btn_exit);
        TextView textView = (TextView) findViewById(R.id.btn_share);
        this.btnShare = textView;
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out best video downloader at: https://play.google.com/store/apps/details?id="+Continue.this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        this.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Continue.this, StartActivity.class);
                   // startActivity(new Intent(getApplicationContext(), Continue.class));
                    startActivity(intent);
                    //finish();
            }
        });
        this.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Continue.this, MainActivity_Snake.class);
                   // startActivity(new Intent(getApplicationContext(), Continue.class));
                    startActivity(intent);
                    //finish();
            }
        });
        this.btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String urlStrRateUs; urlStrRateUs = "market://details?id=" + getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)));*/
                count_rate++;
                if (count_rate>1){
                    String urlStrRateUs; urlStrRateUs = "market://details?id=" + getPackageName();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)));
                }else {
                    startReviewFlow();
                }
            }
        });
        this.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Continue.this, MainActivity_WebView.class);
                // startActivity(new Intent(getApplicationContext(), Continue.class));
                startActivity(intent);*/

               /* Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);*/

               onBackPressed();

            }
        });



        getReviewInfo();

    }
    ReviewManager reviewManager;
    ReviewInfo reviewlnfo = null;
    public static int count_rate =0;
    private void getReviewInfo() {

        reviewManager = ReviewManagerFactory.create(getApplicationContext());
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewlnfo = task.getResult();
            } else {
                Toast.makeText(getApplicationContext(), "In App ReviewFlow failed to start", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void startReviewFlow() {

        if (reviewlnfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(this, reviewlnfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    //Toast.makeText(getApplicationContext(), "In App Rating complete", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "In App Rating failed", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("NotConstructor")
    public void Continue(View view) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        CustomTabsIntent build = builder.build();
        build.intent.setPackage("com.android.chrome");
        build.launchUrl(this, Uri.parse("https://www.google.com/"));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
      finish();
    }
}
