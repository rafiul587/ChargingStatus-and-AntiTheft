package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import kotlinx.coroutines.delay


@Composable
fun StartScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.ic_baseline_battery_charging_full_24),
            contentDescription = "Charging Icon"
        )
        UnlockOkButton(text = stringResource(R.string.start), PaddingValues(vertical = 15.dp, horizontal = 50.dp)) {
            navController.navigate(NavigationItem.BottomNav.route){
                popUpTo(NavigationItem.Start.route){
                    inclusive = true
                }
            }
        }
    }
}


@Preview
@Composable
fun StartScreenPreview() {
    StartScreen(rememberNavController())
}