package com.chargingstatusmonitor.souhadev.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chargingstatusmonitor.souhadev.R;

public class Exit extends AppCompatActivity {
    ViewGroup adContainerNative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exite);
        onBackPressed();
    }
    @Override
    public void onBackPressed() {

        //AppRaterRodo.app_launched(this);
        final Dialog dialogCustomExit = new Dialog(Exit.this, R.style.UploadDialog);
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustomExit.setContentView(R.layout.custom_dialog_layout);
        dialogCustomExit.setCancelable(true);
        dialogCustomExit.show();

        TextView txtTileDialog = dialogCustomExit.findViewById(R.id.textTitle);
        final TextView txtMessageDialog = dialogCustomExit.findViewById(R.id.textDesc);

        TextView btnNegative = dialogCustomExit.findViewById(R.id.btnNegative);
        TextView btnPositive = dialogCustomExit.findViewById(R.id.btnPositive);
      /*  template = dialogCustomExit.findViewById(R.id.my_template);

        if(MyApplication.Ad_Network_t.equalsIgnoreCase("TradPlus")){
            adContainerNative = dialogCustomExit.findViewById(R.id.ad_container);
            adstradplus.loadNormalNative(adContainerNative, MyApplication.NATIVE_ADUNITID_Onback);
            template.setVisibility(View.GONE);

        }else{
            adsManagerW.createUnifiedAds(StartApp.AdmobNativeMain,template);
        }
*/
        txtTileDialog.setText(getString(R.string.exit));
        txtMessageDialog.setText(getString(R.string.description_rate));

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomExit.dismiss();

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);

            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomExit.dismiss();
                rateApp();
            }
        });

    }
    void rateApp() {
        String urlStrRateUs; urlStrRateUs = "market://details?id=" + getPackageName();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)));
    }

}
