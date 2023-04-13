package com.chargingstatusmonitor.souhadev.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargingstatusmonitor.souhadev.BuildConfig
import com.chargingstatusmonitor.souhadev.Continue
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.Title
import com.chargingstatusmonitor.souhadev.ui.theme.Brown
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory


@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current

    val reviewManager = remember {
        ReviewManagerFactory.create(context)
    }

    var reviewInfo: ReviewInfo? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = reviewInfo) {
        reviewInfo?.let {
            reviewManager.launchReviewFlow(context as Activity, it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp, vertical = 10.dp)
    ) {
        MyTradPlus_Native_center()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 52.dp)
    ) {
        Title(text = stringResource(R.string.settings_screen_title))
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsMenu(title = stringResource(R.string.version), subTitle = BuildConfig.VERSION_NAME) {
            }
            SettingsMenu(title = stringResource(R.string.rate_us), subTitle = "", isShowAd = true) {

                Continue.count_rate++
                if (Continue.count_rate > 1) {
                    rateApp(context)
                } else {
                    reviewManager.requestReviewFlow().addOnCompleteListener {
                        if (it.isSuccessful) {
                            reviewInfo = it.result
                        }
                    }
                }
                //rateApp(context)
            }
            SettingsMenu(title = stringResource(R.string.share), subTitle = "") {
                shareApp(context)
            }
            SettingsMenu(title = stringResource(R.string.more_apps), subTitle = "", isShowAd = true) {
                showMoreApps(context)
            }
            /*SettingsMenu(title = stringResource(R.string.privacy), subTitle = "") {
                navController.navigate(NavigationItem.Privacy.route)
            }*/
        }
    }
}

@Composable
fun SettingsMenu(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    isShowAd: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 30.sp,
                color = Brown
            )
            if (isShowAd) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ad",
                    color = Brown.copy(alpha = .7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = subTitle,
            color = Brown.copy(alpha = .7f)
        )
    }

}
private fun shareApp(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
    var shareMessage = "\nLet me recommend you this application\n\n"
    shareMessage =
        """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    context.startActivity(Intent.createChooser(shareIntent, "Choose one"))
}

private fun showMoreApps(context: Context){
    //val str = "https://play.google.com/store/apps/developer?id=YOUR_DEVELOPER_ID"
    val str = "https://play.google.com/store/apps/developer?id=Souha+Dev"
    //https://play.google.com/store/apps/developer?id=Souha+Dev
    //val str = "https://play.google.com/store/apps/dev?id=Gt0"
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(str)))
}

fun rateApp(context: Context) {
    val urlStrRateUs: String
    urlStrRateUs = "market://details?id=" + context.packageName
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlStrRateUs)))
}

