package com.chargingstatusmonitor.souhadev.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.ui.components.MicrophonePermission
import com.chargingstatusmonitor.souhadev.ui.components.TitleWithBackButton
import com.chargingstatusmonitor.souhadev.ui.theme.Brown
import com.chargingstatusmonitor.souhadev.ui.theme.Red


@Composable
fun RecordScreen(navController: NavHostController) {

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 40.dp)
    ) {
        TitleWithBackButton(text = stringResource(R.string.record_screen_title)){
            navController.navigateUp()
        }
        Spacer(Modifier.height(20.dp))
        TabView(tabList = listOf(stringResource(R.string.record_tab_title), stringResource(R.string.my_records_tab_title)), selectedIndex) { index ->
            selectedIndex = index
        }
        if (selectedIndex == 0) MicrophonePermission() else MyRecordsTab()
    }
}

@Composable
fun TabView(
    tabList: List<String>,
    selectedIndex: Int,
    onTabSelect: (Int) -> Unit,
) {

    TabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .padding(vertical = 4.dp),
        divider = { }
    ) {
        tabList.forEachIndexed { index, text ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    onTabSelect(index)
                },
                selectedContentColor = Red,
                unselectedContentColor = Brown.copy(.6f),
                text = {
                    Text(
                        text = text
                    )
                }
            )
        }
    }
}

