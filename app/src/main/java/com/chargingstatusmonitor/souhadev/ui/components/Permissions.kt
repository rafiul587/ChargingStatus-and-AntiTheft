package com.chargingstatusmonitor.souhadev.ui.components

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.screens.HomeContainer
import com.chargingstatusmonitor.souhadev.ui.screens.RecordTab
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermission() {

    // Storage permission state
    val storagePermissionState = rememberPermissionState(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    when (storagePermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            HomeContainer()
        }
        is PermissionStatus.Denied -> {
            if ((storagePermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission

                Dialog(onDismissRequest = { /*TODO*/ }) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        Text(stringResource(R.string.storage_permission_need))
                        Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                            Text(stringResource(id = R.string.request_permission))
                        }
                    }
                }

            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                LaunchedEffect(key1 = Unit) {
                    storagePermissionState.launchPermissionRequest()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophonePermission() {

    // Storage permission state
    val storagePermissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )

    when (storagePermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            RecordTab()
        }
        is PermissionStatus.Denied -> {
            if ((storagePermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission

                Dialog(onDismissRequest = { /*TODO*/ }) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.microphone_permission_need),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                            Text(stringResource(R.string.request_permission))
                        }
                    }
                }

            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                LaunchedEffect(key1 = Unit) {
                    storagePermissionState.launchPermissionRequest()
                }
            }
        }
    }
}