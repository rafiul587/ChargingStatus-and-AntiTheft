package com.chargingstatusmonitor.souhadev.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.data.FileDao
import com.chargingstatusmonitor.souhadev.data.FileEntity
import com.chargingstatusmonitor.souhadev.ui.components.Title
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(navController: NavHostController, dao: FileDao) {

    val (searchText, onSearchValueChange) = remember {
        mutableStateOf("")
    }
    val searchList = remember {
        mutableStateListOf<FileEntity>()
    }
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
        Title(text = stringResource(R.string.search_screen_title))

        Spacer(Modifier.height(20.dp))
        SearchBox(searchText, onSearchValueChange)
        Spacer(Modifier.height(20.dp))

        if (searchText.isEmpty()) {
            DefaultSearchScreen()
        } else {
            LaunchedEffect(key1 = searchText) {
                withContext(Dispatchers.IO) {
                    val list = dao.findByName("%$searchText%")
                    searchList.clear()
                    searchList.addAll(list)
                }
            }
            SearchList(searchList) { uid ->
                navController.navigate("${NavigationItem.Unlock.route}/$uid")
            }
        }

    }
}

@Composable
fun DefaultSearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(250.dp),
            painter = painterResource(id = R.drawable.ic_baseline_search_24),
            contentDescription = "Search Image",
            colorFilter = ColorFilter.tint(Red)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.search_for_ringtones), fontSize = 25.sp
        )
    }
}
@Composable
fun SearchList(ringtones: List<FileEntity>, navigateToUnlock: (Int) -> Unit) {
    val context = LocalContext.current
    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    val mediaPlayer = remember {
        MediaPlayer()
    }
    val coroutineScope = rememberCoroutineScope()

    var job: Job? by remember {
        mutableStateOf(null)
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(ringtones) { index, item ->
            if (item.type == FileType.ASSET) {
                DownLoadItem(
                    context = context,
                    file = item,
                    selectedIndex = selectedIndex,
                    index = index,
                    onSelect = {
                        if (selectedIndex == index && mediaPlayer.isPlaying) {
                            mediaPlayer.stop()
                            selectedIndex = -1
                            return@DownLoadItem
                        }
                        selectedIndex = index
                        if (job != null && job!!.isActive) {
                            job!!.cancel()
                        }
                        job = coroutineScope.launch {
                            playFile(context, mediaPlayer, item.name) {
                                selectedIndex = -1
                            }
                        }
                    },
                    navigateToUnlockScreen = navigateToUnlock
                )
            } else {
                RingtoneItem(
                    selectedIndex = selectedIndex,
                    index = index,
                    fileEntity = item,
                    navigateToUnlock = navigateToUnlock
                ) {
                    if (selectedIndex == index && mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        selectedIndex = -1
                        return@RingtoneItem
                    }
                    selectedIndex = index
                    if (job != null && job!!.isActive) {
                        job!!.cancel()
                    }
                    job = coroutineScope.launch {
                        playRecording(
                            context = context,
                            mediaPlayer = mediaPlayer,
                            path = item.uri
                        ) {
                            selectedIndex = -1
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBox(searchText: String, onSearchValueChange: (String) -> Unit) {
    BasicTextField(modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color.Gray)
        .background(Color(0xffeeeeee))
        .padding(horizontal = 5.dp),
        value = searchText,
        onValueChange = onSearchValueChange,
        textStyle = TextStyle(
            fontSize = 24.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {

        }),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(value = searchText,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_box_hint), fontSize = 24.sp
                    )
                },
                visualTransformation = VisualTransformation.None,
                enabled = true,
                innerTextField = innerTextField,
                singleLine = true,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(10.dp),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(id = R.drawable.ic_baseline_search_24),
                        contentDescription = "Search"
                    )
                })
        })
}
