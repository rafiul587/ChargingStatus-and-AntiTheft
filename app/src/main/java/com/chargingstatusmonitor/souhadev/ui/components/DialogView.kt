package com.chargingstatusmonitor.souhadev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.theme.Brown
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.saveOnConnectFile
import com.chargingstatusmonitor.souhadev.utils.saveOnDisconnectFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun DialogView(fileName: String, fileType: String, onDismiss: () -> Unit) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val annotatedString = buildAnnotatedString {
                append(stringResource(R.string.set))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(" $fileName ")
                }
                append(stringResource(R.string.as_the_ringtone))
            }
            Text(
                text = annotatedString,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = stringResource(R.string.select_the_type))
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            context.saveOnConnectFile("$fileType-$fileName")
                        }
                        onDismiss()
                    }) {
                    Text(
                        text = stringResource(R.string.on_connect).uppercase(Locale.US),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Brown),
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            context.saveOnDisconnectFile("$fileType-$fileName")
                        }
                        onDismiss()
                    }) {
                    Text(
                        text = stringResource(R.string.on_disconnect).uppercase(Locale.US),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}