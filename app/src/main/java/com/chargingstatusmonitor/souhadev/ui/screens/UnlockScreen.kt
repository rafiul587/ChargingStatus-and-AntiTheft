package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.data.FileDao
import com.chargingstatusmonitor.souhadev.data.FileEntity
import com.chargingstatusmonitor.souhadev.ui.components.TitleWithBackButton
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UnlockScreen(
    navController: NavController,
    dao: FileDao,
    id: Int,
) {
    var isLocked by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 40.dp)
    ) {

        TitleWithBackButton(text = stringResource(R.string.unlock_screen_title)) {
            navController.navigateUp()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (isLocked) stringResource(R.string.unlock_for_free) else stringResource(R.string.success),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            val scope = rememberCoroutineScope()
            if (isLocked) {
                UnlockOkButton(
                    text = stringResource(R.string.unlock),
                    PaddingValues(vertical = 15.dp, horizontal = 40.dp)
                ) {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            val count = dao.update(id)
                            if (count > 0) {
                                isLocked = false
                            }
                        }
                    }
                }
            } else {
                UnlockOkButton(
                    text = stringResource(R.string.ok),
                    PaddingValues(vertical = 15.dp, horizontal = 40.dp)
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun UnlockOkButton(
    text: String,
    paddingValues: PaddingValues = PaddingValues(),
    onClick: (String) -> Unit
) {
    Button(
        modifier = Modifier
            .wrapContentHeight(),
        onClick = { onClick(text) },
        colors = ButtonDefaults.buttonColors(backgroundColor = Red),
        contentPadding = paddingValues
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}