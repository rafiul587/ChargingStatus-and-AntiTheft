package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavController) {
    var progress by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(key1 = Unit) {
        for (i in 0..10) {
            delay(200)
            progress = i * 10
        }
        delay(500)
        navController.navigate(NavigationItem.Start.route) {
            popUpTo(NavigationItem.Loading.route) {
                inclusive = true
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 70.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider {
            ProvideTextStyle(value = TextStyle(fontSize = 30.sp)) {
                Text(
                    text = stringResource(R.string.loading)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "$progress%")
            }
        }
    }
}