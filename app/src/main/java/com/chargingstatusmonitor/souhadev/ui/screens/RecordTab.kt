package com.chargingstatusmonitor.souhadev.ui.screens

import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.DotsRecording
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.VoiceRecorder

@Composable
fun RecordTab() {

    var isPressed by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    val recorder = remember {
        VoiceRecorder(context)
    }

    var textCount by remember {
        mutableStateOf("00:00:00")
    }

    var counter by remember {
        mutableStateOf(0L)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        val countDownTimer = remember {
            object : CountDownTimer(Long.MAX_VALUE, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    counter++
                    val millis: Long = (counter % 10) * 10
                    val minutes = (counter / 10) / 60
                    val seconds = (counter / 10) % 60
                    textCount = String.format("%02d:%02d:%02d", minutes, seconds, millis)
                }

                override fun onFinish() {}
            }
        }

        Text(
            text = textCount,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .background(Red, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            try {
                                isPressed = true
                                recorder.onRecord(true)
                                countDownTimer.start()
                                awaitRelease()
                            } finally {
                                isPressed = false
                                recorder.onRecord(false)
                                countDownTimer.cancel()
                                counter = 0L
                                textCount = "00:00:00"
                                Toast.makeText(context, context.getString(R.string.recording_saved), Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                }
                .size(160.dp)

        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(64.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_voice_24),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "Voice Icon"
                )
                if (isPressed) {
                    Spacer(modifier = Modifier.height(5.dp))
                    DotsRecording()
                }
            }
        }
    }
}
