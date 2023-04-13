package com.chargingstatusmonitor.souhadev.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargingstatusmonitor.souhadev.AdsManagerTradePlus
import com.chargingstatusmonitor.souhadev.MyApplication
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.TitleWithBackButton
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener


@Composable
fun RingtoneScreen(navController: NavController) {

    val context = LocalContext.current
    AdsManagerTradePlus.loadInterstitial_main(context as Activity?, MyApplication.TADUNIT_Inter_Main)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 40.dp)
    ) {
        TitleWithBackButton(text = stringResource(R.string.ringtone_screen_title)){
            navController.navigateUp()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalArrangement = spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonContainer(text = stringResource(R.string.phone)) {
                // mTpInterstitial_main.showAd(activity,"");
                //展示
                if (AdsManagerTradePlus.adsCounterT % 8 == 0) {
                    AdsManagerTradePlus.adsCounterT++

                    if (AdsManagerTradePlus.mTpInterstitial_main.isReady) {
                        AdsManagerTradePlus.mTpInterstitial_main.showAd(context as Activity?, "")
                    }else {
                        navController.navigate(NavigationItem.Phone.route)
                    }

                    AdsManagerTradePlus.mTpInterstitial_main.setAdListener(object : InterstitialAdListener {
                        override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                            // Called when an ad is loaded after 'LoadAd'.
                            // Toast.makeText(Splash.this, "test inters work", Toast.LENGTH_LONG).show();
                            navController.navigate(NavigationItem.Phone.route)
                        }

                        override fun onAdClicked(tpAdInfo: TPAdInfo) {
                            //  Called when a rewarded ad is clicked.
                        }

                        override fun onAdImpression(tpAdInfo: TPAdInfo) {
                            // Called when a rewarded ad starts playing.
                        }

                        override fun onAdFailed(tpAdError: TPAdError) {
                            // Called when an ad is failed after 'LoadAd'.
                            Log.d("Tradplusad_ERROR_CODE", "tradplusad error code inters: $tpAdError")
                            // Toast.makeText(Splash.this, "test inters", Toast.LENGTH_LONG).show();
                            navController.navigate(NavigationItem.Phone.route)
                            // load(activity);
                        }

                        override fun onAdClosed(tpAdInfo: TPAdInfo) {
                            // Called when a rewarded ad is closed.
                            // load(activity);
                        }

                        override fun onAdVideoError(tpAdInfo: TPAdInfo, tpAdError: TPAdError) {

                            //load(activity);
                        }

                        override fun onAdVideoStart(p0: TPAdInfo?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAdVideoEnd(p0: TPAdInfo?) {
                            TODO("Not yet implemented")
                        }
                    })
                } else {
                    navController.navigate(NavigationItem.Phone.route)
                }


            }
            ButtonContainer(text = stringResource(R.string.download)) {
                navController.navigate(NavigationItem.Download.route)
            }
            ButtonContainer(text = stringResource(R.string.record)) {
                navController.navigate(NavigationItem.Record.route)
            }
        }
    }
}

@Composable
fun ButtonContainer(text: String, onClick: (String) -> Unit) {

    Button(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick(text) },
        colors = ButtonDefaults.buttonColors(backgroundColor = Red),
        contentPadding = PaddingValues(15.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun ScreenPreview() {
    RingtoneScreen(rememberNavController())
}