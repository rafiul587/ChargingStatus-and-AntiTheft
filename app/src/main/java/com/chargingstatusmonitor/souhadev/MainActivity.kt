package com.chargingstatusmonitor.souhadev

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chargingstatusmonitor.souhadev.ui.navigation.BottomNavigation
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationGraph
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import com.chargingstatusmonitor.souhadev.ui.theme.ChargingStatusTheme
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.MyService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.tradplus.ads.open.banner.TPBanner
import com.tradplus.ads.open.nativead.TPNativeBanner


class MainActivity : ComponentActivity() {
    private var serviceIntent: Intent? = null
    var activity: MainActivity? = this@MainActivity
    var mActivity: MainActivity? = this@MainActivity
    private val reviewManager by lazy {
        ReviewManagerFactory.create(this)
    }
    private fun initFlow() {
        reviewManager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                launchFlow(task.result)
            } else {
                val message = task.exception?.message
                Log.d(TAG, "initFlow: $message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchFlow(reviewInfo: ReviewInfo) {
        val flow = reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            Toast.makeText(this, "Flow Completed", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initFlow()
        if (serviceIntent == null) {
            // start service
            serviceIntent = Intent(this, MyService::class.java)
            startService(serviceIntent)
        }

        setContent {
            ChargingStatusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //MyBoxTradPlus()
                    //AdsManagerTradePlus.loadBanner(activity)
                    AdsManagerTradePlus.loadNative(activity,MyApplication.NATIVE_ADUNITID_Onback)
                    //MyTradPlus_Banner()
                    MainContainer()
                    BackHandler {
                        // progressBar.setVisibility(View.GONE);
                        /*Toast.makeText(
                            this@MainActivity,
                            "We are 3 making some system upgrades, please try again later",
                            Toast.LENGTH_SHORT
                        ).show()*/
                        /*serviceIntent = Intent(this, Exit::class.java)
                        startService(serviceIntent)*/
                        AdsManagerTradePlus.onBackPressed(activity)

                    }
                    //Greeting("App will play a audio when charging status changes")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }
}

@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    Scaffold(
        // Bar Bottom
        topBar = {
            if (currentRoute?.destination?.hierarchy?.any { it.route == NavigationItem.Loading.route || it.route == NavigationItem.Start.route } != true) {
                BottomNavigation(navController = navController)

            }
        }
        // End Bar Bottom
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ){
            Scaffold(
                // Bar Bottom
                bottomBar = {
                    MyTradPlus_Banner_center()
                    //MyTradPlus_Native_center()
                }
                // End Bar Bottom
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    NavigationGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun MyTradPlus_Banner() {
    Box(modifier = Modifier.fillMaxWidth()){
        /*Text (
            modifier = Modifier.align(Alignment.Center),
            text = "One ihsdiqh qfhqos qkhfoqhf qihfoqh qihsfoih oqihfoi",
            style = TextStyle(background = Color.Yellow),
            fontSize = 22.sp)*/

        /*AndroidView(
            factory = { context ->
                TPBanner(context).apply {
                    loadAd("A24091715B4FCD50C0F2039A5AF7C4BB");
                }
               *//* AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    //adUnitId = context.getString(R.string.ad_id_banner)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }*//*
            }
        )*/

        AndroidView(
            factory = { context ->
                    TPBanner(context).apply {
                        loadAd(MyApplication.TADUNIT_Banner);
                }
            }
        )
       /* AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    //adUnitId = context.getString(R.string.ad_id_banner)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )*/
    }
}

@Composable
fun MyTradPlus_Native_center(modifier: Modifier = Modifier) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "Advert Here",
        )
    } else {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                TPNativeBanner(context).apply {
                    loadAd(MyApplication.TADUNIT_Native_Banner);
                }
            }
        )
    }
}

@Composable
fun MyTradPlus_Banner_center(modifier: Modifier = Modifier) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "Advert Here",
        )
    } else {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                TPBanner(context).apply {
                    loadAd(MyApplication.TADUNIT_Banner);
                }
            }
        )
    }
}

@Composable
fun MyBox(){
    Box(modifier = Modifier.fillMaxWidth()){
        /*Text (
            modifier = Modifier.align(Alignment.Center),
            text = "One ihsdiqh qfhqos qkhfoqhf qihfoqh qihsfoih oqihfoi",
            style = TextStyle(background = Color.Yellow),
            fontSize = 22.sp)*/
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    //adUnitId = context.getString(R.string.ad_id_banner)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChargingStatusTheme {
        MainContainer()
    }
}