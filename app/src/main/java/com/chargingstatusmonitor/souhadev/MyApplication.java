package com.chargingstatusmonitor.souhadev;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chargingstatusmonitor.souhadev.data.local.AppDatabase;
import com.chargingstatusmonitor.souhadev.data.local.FileDao;
import com.chargingstatusmonitor.souhadev.data.local.AppDataStore;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.tradplus.ads.open.TradPlusSdk;

import org.json.JSONObject;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MyApplication extends Application {
    // AdNetwork TradPlus,admob,applovin
    //public static  String AdNetwork = "TradPlus";
    //public static String TADUNIT="44273068BFF4D8A8AFF3D5B11CBA3ADE";
    public static String TADUNIT = "DDB38C48565855382C1BB55FD853A56B";
    //public static String TADUNIT_Inter_Splash="E609A0A67AF53299F2176C3A7783C46D";
    public static String TADUNIT_Inter_Splash = "1353D4779ED35B3DF70A9D018537CBF8";
    //public static String TADUNIT_Inter_Main="E609A0A67AF53299F2176C3A7783C46D";
    public static String TADUNIT_Inter_Main = "93F1E5C36AF4E4C26D6BE225597B2BBC";

    public static String TADUNIT_Open_Splash = "E";

    //public static String TADUNIT_Banner="A24091715B4FCD50C0F2039A5AF7C4BB";
    public static String TADUNIT_Banner = "AC0AD229DD43AE63DA5C5DBFFAE8C411";
    public static String TADUNIT_Banner_Details = "A2";
    public static String TADUNIT_Banner_Game = "A228D36F495202AE2EA42C5E16256CC6";
    public static String TADUNIT_Banner_Webview = "F4DC832659B2C514DB591B1C7E9684EE";

    //public static String TADUNIT_Native_Banner="9F4D76E204326B16BD42FA877AFE8E7D";
    public static String TADUNIT_Native_Banner = "418263F61D1344A85EB8C1CE81D2DBFD";
    public static String TADUNIT_Native_Banner_details = "9F";
    public static String TADUNIT_Native_Banner_Game = "D6D64E284C83753A2B6E48E7B3D28B34";
    public static String TADUNIT_Native_Banner_Webview = "ECA632DD62051DC10D30DCED051E46C3";

    //public static String NATIVE_ADUNITID_Onback = "DDBF26FBDA47FBE2765F1A089F1356BF";
    public static String NATIVE_ADUNITID_Onback = "CD32C65E64322AA7CBC2A0E30D2AECDC";

    public static String url = "https://ia601501.us.archive.org/33/items/com.chargingsound.souhadev/site.json";

    private static final String ONESIGNAL_APP_ID = "b2996a26-9aeb-4678-90e1-16f328ed485a";
    public static Boolean notification_clicked = false;
    private FileDao dao;
    private AppDataStore appDataStore;

    @Override
    public void onCreate() {
        super.onCreate();

        dao = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class, getApplicationContext().getString(R.string.room_database_name)
        ).build().fileEntityDao();
        appDataStore = new AppDataStore(getApplicationContext());

        RxJavaPlugins.setErrorHandler(e -> {

            if (e instanceof UndeliverableException) {
                // As UndeliverableException is a wrapper, get the cause of it to get the "real" exception
                e = e.getCause();
                Log.d("TAG", "onCreate: "+ e.getMessage());
            }

            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }

            Log.e("TAG", "Undeliverable exception received, not sure what to do", e);
        });

        TradPlusSdk.initSdk(MyApplication.this, TADUNIT);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
                //JSONObject data = osNotificationOpenedResult.getNotification().getAdditionalData();
                //Log.i("OneSignalExample", "Notification Data: " + data);
                //String notification_topic;
                notification_clicked = true;
                /*if (data != null) {
                    notification_topic = data.optString("notification_topic", null);
                    if (notification_topic != null) {
                        OneSignal.addTrigger("notification_topic", notification_topic);
                    }
                }*/
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMyIdsFromServers();
            }
        }, 4500);

    }

    public static String SiteURL = "www.blogspot.com";

    private void getMyIdsFromServers() {
        //  imageView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_left));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object = response.getJSONObject("myads");
                    SiteURL = object.getString("SiteURL");

                } catch (Exception e) {
                    e.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(MyApplication.this, "We are making some system upgrades, please try again later", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(MyApplication.this, message, Toast.LENGTH_SHORT).show();
                    Log.i("Error Network", error.getMessage());
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                    Toast.makeText(MyApplication.this, message, Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                    Toast.makeText(MyApplication.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public FileDao getDao() {
        return dao;
    }

    public AppDataStore getDataStore() {
        return appDataStore;
    }
}
