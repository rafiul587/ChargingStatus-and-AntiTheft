package com.chargingstatusmonitor.souhadev.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.theme.Brown

@Composable
fun Title(text: String) {
    Text(
        text = text,
        color = Brown,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TitleWithBackButton(text: String, onBack: () -> Unit) {
    Row() {
        IconButton(onClick = { onBack() }) {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                tint = Brown,
                contentDescription = "Back Button Icon")
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = text,
            color = Brown,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewTitleWithButton() {
    TitleWithBackButton(text = "Back"){}
    
}