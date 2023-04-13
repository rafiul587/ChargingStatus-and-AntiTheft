package com.chargingstatusmonitor.souhadev.ui.screens

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.widget.Toast
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun DownLoadScreen(navController: NavHostController, dao: FileDao) {

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        getAllFilesFromAssets(dao, context = context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 40.dp)
    ) {
        TitleWithBackButton(text = stringResource(R.string.download_screen_title)) {
            navController.navigateUp()
        }
        Spacer(Modifier.height(20.dp))

        val files by dao.getAllAssetsFiles().collectAsState(null)

        if (files.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    text = stringResource(R.string.first_time_loading_msg)
                )
            }
        } else {
            DownloadList(context, ringtones = files!!, navController)
        }
    }
}

@Composable
fun DownloadList(context: Context, ringtones: List<FileEntity>, navController: NavHostController) {
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
                }
            ) { uid ->
                navController.navigate("${NavigationItem.Unlock.route}/$uid")
            }
        }
    }
}

@Composable
fun DownLoadItem(
    context: Context,
    file: FileEntity,
    selectedIndex: Int,
    index: Int,
    onSelect: () -> Unit,
    navigateToUnlockScreen: (Int) -> Unit
) {

    var startDownload by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        DialogView(file.name, "${FileType.ASSET}-${file.uri}") {
            showDialog = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (file.isLocked) {
                    navigateToUnlockScreen(file.uid)
                } else {
                    onSelect()
                }
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(36.dp),
            painter = painterResource(id = R.drawable.ic_baseline_audio_file_24),
            tint = Red,
            contentDescription = "File Icon"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = file.name,
                fontSize = 20.sp
            )
            Text(text = file.duration)
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

        if (file.isLocked) {
            Icon(
                modifier = Modifier
                    .padding(10.dp),
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
            IconButton(onClick = { startDownload = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_file_download_24),
                    contentDescription = "Download Icon"
                )
            }

        }

        if (startDownload) {
            LaunchedEffect(startDownload) {
                startDownload(fileName = file.name, context = context) {
                    startDownload = false
                }
            }
        }
    }
}

suspend fun getAllFilesFromAssets(dao: FileDao, context: Context) {
    withContext(Dispatchers.IO) {
        val rowCount = dao.getAssetRowCount()
        if (rowCount <= 0) {
            val list = mutableListOf<FileEntity>()
            val mmr = MediaMetadataRetriever()
            context.resources.assets.list(context.getString(R.string.asset_folder_name))
                ?.forEachIndexed { index, item ->
                    try {
                        val d = context.assets.openFd("${context.getString(R.string.asset_folder_name)}/$item")
                        mmr.setDataSource(d.fileDescriptor, d.startOffset, d.length)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    list.add(
                        FileEntity(
                            name = item,
                            duration = getDuration(duration?.toLong() ?: 0),
                            type = FileType.ASSET,
                            uri = "asset",
                            isLocked = index > 0
                        )
                    )
                }
            mmr.release()
            dao.insertAll(list)
        }
    }
}

fun startDownload(context: Context, fileName: String, onComplete: () -> Unit) {
    val directoryTest = File(
        getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        context.getString(R.string.download_folder_name)
    )

    if (!directoryTest.exists()) {
        directoryTest.mkdirs()
    }
    try {
        val `in` = context.assets.open("${context.getString(R.string.asset_folder_name)}/$fileName")
        val file = File(directoryTest.path.toString() + "/$fileName")
        if (file.exists()) {
            file.delete()
        }
        val out = FileOutputStream(file)
        val buff = ByteArray(1024)
        var read: Int
        while (`in`.read(buff).also { read = it } > 0) {
            out.write(buff, 0, read)
        }
        `in`.close()
        out.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    onComplete()
    Toast.makeText(
        context,
        context.getString(R.string.download_success),
        Toast.LENGTH_SHORT
    ).show()
}

fun playFile(
    context: Context,
    mediaPlayer: MediaPlayer,
    fileName: String,
    onFinish: () -> Unit = {}
) {
    val descriptor =
        context.assets.openFd("${context.getString(R.string.asset_folder_name)}/${fileName}")
    if (mediaPlayer.isPlaying) {
        mediaPlayer.stop()
    }
    mediaPlayer.reset()
    mediaPlayer.setDataSource(
        descriptor.fileDescriptor,
        descriptor.startOffset,
        descriptor.length
    )
    descriptor.close()
    mediaPlayer.prepareAsync()

    mediaPlayer.setOnCompletionListener {
        onFinish()
    }
    mediaPlayer.setOnPreparedListener {
        mediaPlayer.start()
    }
}
