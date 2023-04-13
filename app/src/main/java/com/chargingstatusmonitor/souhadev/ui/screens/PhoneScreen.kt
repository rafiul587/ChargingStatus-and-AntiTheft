package com.chargingstatusmonitor.souhadev.ui.screens

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.data.FileDao
import com.chargingstatusmonitor.souhadev.data.FileEntity
import com.chargingstatusmonitor.souhadev.ui.components.DialogView
import com.chargingstatusmonitor.souhadev.ui.components.TitleWithBackButton
import com.chargingstatusmonitor.souhadev.ui.navigation.NavigationItem
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


@Composable
fun PhoneScreen(
    navController: NavHostController, dao: FileDao
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp, top = 40.dp)
    ) {
        val context = LocalContext.current

        val fileList by dao.getAllRingtones().collectAsState(null)

        LaunchedEffect(Unit) {
            getRingtonesFromPhone(context, dao)
        }
        TitleWithBackButton(text = stringResource(R.string.phone_screen_title)) {
            navController.navigateUp()
        }
        Log.d("TAG", "PhoneScreen: $fileList")
        Spacer(Modifier.height(20.dp))

        if (fileList.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    text = stringResource(if(fileList == null) R.string.first_time_loading_msg else R.string.no_ringtone)
                )
            }
        } else {
            RingtoneList(ringtones = fileList!!) { uid ->
                navController.navigate("${NavigationItem.Unlock.route}/$uid")
            }
        }
    }
}

@Composable
fun RingtoneList(ringtones: List<FileEntity>, navigateToUnlock: (Int) -> Unit) {
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
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(ringtones) { index, item ->
            RingtoneItem(
                fileEntity = item,
                navigateToUnlock = navigateToUnlock,
                selectedIndex = selectedIndex,
                index = index
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
                    playRecording(context, mediaPlayer, item.uri) {
                        selectedIndex = -1
                    }
                }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_STOP) {
                mediaPlayer.stop()
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun RingtoneItem(
    fileEntity: FileEntity,
    navigateToUnlock: (Int) -> Unit,
    selectedIndex: Int,
    index: Int,
    onSelect: () -> Unit
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        DialogView(fileName = fileEntity.name, "${FileType.RINGTONE}-${fileEntity.uri}") {
            showDialog = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (fileEntity.isLocked) {
                    navigateToUnlock(
                        fileEntity.uid
                    )
                } else {
                    onSelect()
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fileEntity.name, fontSize = 20.sp
            )
            Text(text = fileEntity.duration)
        }
        if (selectedIndex == index) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                tint = Red,
                contentDescription = "Pause Icon"
            )
        }
        Spacer(modifier = Modifier.width(10.dp))

        if (fileEntity.isLocked) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = R.drawable.ic_baseline_lock_24),
                contentDescription = "Lock Icon"
            )
        } else {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                    contentDescription = "Set Ringtone Icon"
                )
            }
        }
    }
}

suspend fun getRingtonesFromPhone(context: Context, dao: FileDao) {
    withContext(Dispatchers.IO) {
        val rowCount = dao.getRingtoneRowCount()
        if (rowCount <= 0) {
            val manager = RingtoneManager(context)
            manager.setType(RingtoneManager.TYPE_RINGTONE)
            val cursor: Cursor = manager.cursor
            val list = mutableListOf<FileEntity>()
            var counter = 0
            val mmr = MediaMetadataRetriever()
            while (cursor.moveToNext()) {
                counter++
                val ringtoneTitle: String = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val ringtoneUri: String =
                    cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
                        RingtoneManager.ID_COLUMN_INDEX
                    )
                try {
                    mmr.setDataSource(context, Uri.parse(ringtoneUri))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                list.add(
                    FileEntity(
                        name = ringtoneTitle,
                        duration = getDuration(duration?.toLong() ?: 0),
                        type = FileType.RINGTONE,
                        uri = ringtoneUri,
                        isLocked = counter > cursor.count / 2
                    )
                )
            }
            mmr.release()
            dao.insertAll(list)
        }
    }
}
