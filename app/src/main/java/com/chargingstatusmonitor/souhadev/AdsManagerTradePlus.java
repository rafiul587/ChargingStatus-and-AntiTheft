package com.chargingstatusmonitor.souhadev;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeBanner;


public class AdsManagerTradePlus {

    private AppCompatActivity mActivity;
    public static int adsCounterT =  0;
    public AdsManagerTradePlus(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    private static final String TAG = "tradpluslog";
    RelativeLayout banner_container,banner_container_Top;

    public void showbanner_tradplus(final Activity activity , String AD_Banner){
        ViewGroup adContainer;
        TPBanner tpBanner;
        adContainer = activity.findViewById(R.id.ad_container);
        // new TPBanner，也可以把TPBanner写在开发者的xml中，这里改成findViewById
        tpBanner = new TPBanner(activity);
        ViewGroup.LayoutParams params = adContainer.getLayoutParams();
        params.height = 150;
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.d("Tradplusad_ERROR_CODE", "tradplusad error code: " + error);
                params.height = 0;
            }
            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
            }
        });
        adContainer.addView(tpBanner);
        // tpBanner.loadAd(SplashActivity.TADUNIT_Banner);
        tpBanner.loadAd(AD_Banner);
        tpBanner.setVisibility(View.VISIBLE);
    }


    //-----------------Native Banner---------------//

    public void NormalNativeBanner(final Activity activity , String AD_Native_Banner) {
        TPNativeBanner tpNativeBanner;
        ViewGroup adContainern;
        adContainern = activity.findViewById(R.id.ad_containerNative);
        tpNativeBanner = new TPNativeBanner(activity);
        ViewGroup.LayoutParams params = adContainern.getLayoutParams();
        params.height = 150;
        tpNativeBanner.setAdListener(new BannerAdListener(){
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
                adContainern.setVisibility(View.GONE);
            }
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded nativebanner: " + tpAdInfo.adSourceName + "加载成功");
                //Toast.makeText(mActivity, "We are 3 making some system upgrades, please try again later", Toast.LENGTH_SHORT).show();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }
            @Override
            public void onAdLoadFailed(TPAdError tpAdInfo) {
                Log.i(TAG, "onAdLoadFailed:加载失败: code : "+ tpAdInfo.getErrorCode() + ", msg :" + tpAdInfo.getErrorMsg());
                params.height = 0;
            }
            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "被关闭");
            }
        });
        adContainern.addView(tpNativeBanner);
        tpNativeBanner.loadAd(AD_Native_Banner);
    }


    //------------------------Native-----------------------//
    static TPNative tpNative;
    public static void loadNormalNative(final Activity activity,ViewGroup adContainerNative, String AD_Native) {

        loadNative(activity,AD_Native);

        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                tpNative.showAd(adContainerNative, R.layout.tp_native_ad_list_item,"");

            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: "+ tpAdInfo.adSourceName + "被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: "+ tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "广告关闭");
            }
        });

        //tpNative.loadAd();
    }
    public static void loadNative(final Activity activity,String AD_Native) {

        tpNative = new TPNative(activity,AD_Native);
        tpNative.loadAd();
    }
    //------------------------End Native-----------------------//

    static ViewGroup adContainerNative;
    public static void onBackPressed(final Activity activity) {

        //AppRaterRodo.app_launched(this);
        final Dialog dialogCustomExit = new Dialog(activity, R.style.UploadDialog);
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustomExit.setContentView(R.layout.custom_dialog_layout);
        dialogCustomExit.setCancelable(true);
        dialogCustomExit.show();

        TextView txtTileDialog = dialogCustomExit.findViewById(R.id.textTitle);
        final TextView txtMessageDialog = dialogCustomExit.findViewById(R.id.textDesc);

        TextView btnNegative = dialogCustomExit.findViewById(R.id.btnNegative);
        TextView btnPositive = dialogCustomExit.findViewById(R.id.btnPositive);

        adContainerNative = dialogCustomExit.findViewById(R.id.ad_container);
        loadNormalNative(activity,adContainerNative, MyApplication.NATIVE_ADUNITID_Onback);/*  template = dialogCustomExit.findViewById(R.id.my_template);

        if(MyApplication.Ad_Network_t.equalsIgnoreCase("TradPlus")){
            adContainerNative = dialogCustomExit.findViewById(R.id.ad_container);
            adstradplus.loadNormalNative(adContainerNative, MyApplication.NATIVE_ADUNITID_Onback);
            template.setVisibility(View.GONE);

        }else{
            adsManagerW.createUnifiedAds(StartApp.AdmobNativeMain,template);
        }
*/
        txtTileDialog.setText(activity.getString(R.string.exit));
        txtMessageDialog.setText(activity.getString(R.string.description_rate));

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomExit.dismiss();

                /*Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                activity.startActivity(intent);*/
                activity.finish();

            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustomExit.dismiss();
                rateApp(activity);
            }
        });

    }
    static void rateApp(final Activity activity) {
        String urlStrRateUs; urlStrRateUs = "market://details?id=" + activity.getPackageName();
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)));
    }

    //------------------------Interstitial-----------------------//
    public static TPInterstitial mTpInterstitial;
    public static TPInterstitial mTpInterstitial_main;
    public  static void loadInterstitial(final Activity activity,String Ad_Inter) {
        // if (MyApplicationRodo.TradplusShow = true){
        mTpInterstitial = new TPInterstitial(activity, Ad_Inter);
        //  mTpInterstitial = new TPInterstitial(this, "1353D4779ED35B3DF70A", true);

        mTpInterstitial.loadAd();
        // }
    }
    public  static void loadInterstitial_main(final Activity activity,String Ad_Inter) {
        // if (MyApplicationRodo.TradplusShow = true){
        mTpInterstitial_main = new TPInterstitial(activity, Ad_Inter);
        //  mTpInterstitial = new TPInterstitial(this, "1353D4779ED35B3DF70A", true);

        mTpInterstitial_main.loadAd();
        // }
    }
    public  static void show_inter(final Activity activity, final Intent intent,final int x) {

        //adsCounterT++;

       // if(adsCounterT % 3 == 0){
            if (!mTpInterstitial.isReady()) {
                // 无可用的广告，如果开启自动加载，可以什么都不做，如果没开启自动加载，可以在这里调用一次load（注意不要频繁触发）
                Log.i(TAG, "isReady: 无可用广告");
                //tv.setText("isReady: 无可用广告");
                activity.startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                if (x==1){
                    activity.finish();
                }
            }else{
                //展示
                mTpInterstitial.showAd(activity, "");
            }
            // mTpInterstitial.showAd(activity,"");
            mTpInterstitial.setAdListener(new InterstitialAdListener() {

                @Override
                public void onAdLoaded(TPAdInfo tpAdInfo) {
                    // Called when an ad is loaded after 'LoadAd'.
                    // Toast.makeText(Splash.this, "test inters work", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAdClicked(TPAdInfo tpAdInfo) {
                    //  Called when a rewarded ad is clicked.
                }

                @Override
                public void onAdImpression(TPAdInfo tpAdInfo) {
                    // Called when a rewarded ad starts playing.
                }

                @Override
                public void onAdFailed(TPAdError tpAdError) {
                    // Called when an ad is failed after 'LoadAd'.
                    Log.d("Tradplusad_ERROR_CODE", "tradplusad error code inters: " + tpAdError);
                    // Toast.makeText(Splash.this, "test inters", Toast.LENGTH_LONG).show();
                    activity.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (x==1){
                        activity.finish();
                    }

                    // load(activity);

                }

                @Override
                public void onAdClosed(TPAdInfo tpAdInfo) {
                    // Called when a rewarded ad is closed.
                    activity.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (x==1){
                        activity.finish();
                    }
                   // mTpInterstitial.onDestory();
                    // load(activity);
                }

                @Override
                public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                    activity.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if (x==1){
                        activity.finish();
                    }
                    //load(activity);
                }

                @Override
                public void onAdVideoStart(TPAdInfo tpAdInfo) {

                }

                @Override
                public void onAdVideoEnd(TPAdInfo tpAdInfo) {

                }
            });
        /*}else{
            loadInterstitial(activity, MyApplication.TADUNIT_Inter_Splash);
            activity.startActivity(intent);
            if (x==1){
                activity.finish();
            }
        }*/


    }

    //------------------------End Interstitial-----------------------//

}

