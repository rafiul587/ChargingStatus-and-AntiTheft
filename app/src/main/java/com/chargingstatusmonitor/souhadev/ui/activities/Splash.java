package com.chargingstatusmonitor.souhadev.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chargingstatusmonitor.souhadev.AdsManagerTradePlus;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.adview.MainActivity_WebView;

public class Splash extends AppCompatActivity {

    String info_user , name_user;
    private final String TAG = Splash.class.getSimpleName();
    AdsManagerTradePlus adstradplus;
    @Override
    protected void onCreate(Bundle Splash){
        super.onCreate(Splash);
        setContentView(R.layout.splash);
        adstradplus = new AdsManagerTradePlus(this);
        adstradplus.loadInterstitial(this, MyApplication.TADUNIT_Inter_Splash);

        Splash();
    }
    @SuppressLint("NotConstructor")
    public void Splash(){
        Thread splash = new Thread(){
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while ((waited < 2000)) {
                        sleep(5000);
                        waited += 7000;
                    }
                } catch (InterruptedException e) {
                    e.toString();
                } finally {

                    if (MyApplication.notification_clicked==true){
                        Intent  i2 =  new Intent(Splash.this, MainActivity_WebView.class);
                        AdsManagerTradePlus.show_inter(Splash.this, i2,1);
                    } else {
                        Intent  i2 =  new Intent(Splash.this, Continue.class);
                        AdsManagerTradePlus.show_inter(Splash.this, i2,1);
                    }

                        //adsManagerW.showNextFINISH(SplashActivityRodo.this,i2)

                    /*startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();*/
                }
            }

        };
        splash.start();
        }

}
