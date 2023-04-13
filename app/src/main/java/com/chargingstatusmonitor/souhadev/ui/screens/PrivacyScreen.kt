package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.TitleWithBackButton

@Composable
fun PrivacyScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 40.dp)
    ) {
        TitleWithBackButton(text = stringResource(R.string.privacy_screen_title)){
            navController.navigateUp()
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.privacy_policy),
            textAlign = TextAlign.Center
        )
    }
}