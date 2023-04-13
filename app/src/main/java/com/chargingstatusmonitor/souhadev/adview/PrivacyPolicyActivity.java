
package com.chargingstatusmonitor.souhadev.adview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chargingstatusmonitor.souhadev.R;

public class PrivacyPolicyActivity extends Activity {

    WebView txtInformtation;
    ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_privacy_policy);

        findViews();
        initViews();

    }

    private void findViews() {

        imgBack = findViewById(R.id.ic_back);
    }


    private void initViews() {

        txtInformtation = findViewById(R.id.txtInformtation);
        txtInformtation.loadUrl("www.google.com");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

}