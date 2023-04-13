package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.chargingstatusmonitor.souhadev.MyApplication
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.StoragePermission
import com.chargingstatusmonitor.souhadev.ui.components.Title
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.getOnConnectFile
import com.chargingstatusmonitor.souhadev.utils.getOnDisconnectFile
import com.tradplus.ads.open.nativead.TPNativeBanner


@Composable
fun HomeScreen() {
    StoragePermission()
}

@Composable
fun HomeContainer() {

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

        val context = LocalContext.current
        val connectText by context.getOnConnectFile().collectAsState(null)
        val disConnectText by context.getOnDisconnectFile().collectAsState(null)
        Title(text = stringResource(R.string.home_screen_title))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.home_screen_instructions),
                textAlign = TextAlign.Center,
                lineHeight = 1.6.em,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.on_connect),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${connectText?.split("-")?.get(2)}",
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = stringResource(id = R.string.on_disconnect),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${disConnectText?.split("-")?.get(2)}",
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
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

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}