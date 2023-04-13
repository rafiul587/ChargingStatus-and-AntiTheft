package com.chargingstatusmonitor.souhadev.ui.screens

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.model.FileModel
import com.chargingstatusmonitor.souhadev.ui.components.DialogView
import com.chargingstatusmonitor.souhadev.ui.theme.Red
import com.chargingstatusmonitor.souhadev.utils.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyRecordsTab() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val context = LocalContext.current

        val recordings = remember {
            mutableStateListOf<FileModel>()
        }
        LaunchedEffect(Unit) {
            recordings.clear()
            recordings.addAll(getMyRecords(context))
        }
        MyRecordList(recordings = recordings)
    }
}

@Composable
fun MyRecordList(recordings: List<FileModel>) {

    var selectedIndex by remember {
        mutableStateOf(-1)
    }

    val mediaPlayer = remember { MediaPlayer() }
    val coroutineScope = rememberCoroutineScope()

    var job: Job? by remember {
        mutableStateOf(null)
    }

    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(recordings) { index, item ->
            MyRecordItem(item, selectedIndex, index) {
                if (selectedIndex == index && mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    selectedIndex = -1
                    return@MyRecordItem
                }
                selectedIndex = index
                if (job != null && job!!.isActive) {
                    job!!.cancel()
                }
                job = coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        playRecording(
                            context = context,
                            mediaPlayer = mediaPlayer,
                            path = item.path
                        ) {
                            selectedIndex = -1
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyRecordItem(file: FileModel, selectedIndex: Int, index: Int, onSelect: () -> Unit) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        DialogView(file.name, "${FileType.RECORD}-${file.path}") {
            showDialog = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    },
                    onTap = {
                        onSelect()
                    })
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
            modifier = Modifier.weight(1f)
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
    }
}

suspend fun getMyRecords(context: Context): List<FileModel> {
    val recordingList = mutableListOf<FileModel>()
    withContext(Dispatchers.IO) {

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val selection: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.RELATIVE_PATH +
                    " LIKE ? "
        } else {
            MediaStore.Audio.Media.DATA +
                    " LIKE ? "
        }
        val selectionArgs = arrayOf(
            "%" + context.getString(R.string.recording_folder_name) + "%"
        )
        val sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " DESC "
        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            //val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)
                val duration = getDuration(context, path)
                val fileModel = FileModel(id, name, duration, path)
                recordingList.add(fileModel)
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val folderName = context.getString(R.string.recording_folder_name)
            val folder = File(Environment.getExternalStorageDirectory(), folderName)
            if (folder.exists() && folder.isDirectory) {
                val files = folder.listFiles { dir, name ->
                    name.toLowerCase(Locale.getDefault()).endsWith(".mp3")
                }
                files?.forEach { file ->
                    val id = file.name
                    val name = file.name
                    val path = file.absolutePath
                    val duration = getDuration(context, path)
                    val fileModel = FileModel(id, name, duration, path)
                    if (recordingList.find { it.name == fileModel.name } == null) {
                        recordingList.add(fileModel)
                    }
                }
            }
        }

    }
    return recordingList
}

fun getDuration(context: Context, filePath: String): String {
    val retriever = MediaMetadataRetriever()
    var duration = 0L
    try {
        retriever.setDataSource(context, Uri.parse(filePath))
        duration =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                ?: 0L
    } catch (e: Exception) {
        Log.d("TAG", "getDuration: ${e.message}")
    } finally {
        retriever.release()
    }
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
}

fun getDuration(duration: Long): String {
    val minutes = (duration / 1000) / 60
    val seconds = (duration / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun playRecording(
    context: Context,
    mediaPlayer: MediaPlayer,
    path: String,
    onFinish: () -> Unit = {},
) {
    if (mediaPlayer.isPlaying) {
        mediaPlayer.stop()
    }
    try {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, path.toUri())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            onFinish()
        }
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
    } catch (e: Exception) {
        if (e.message?.contains("EACCES") == true) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(context, "Failed to play!", Toast.LENGTH_SHORT).show()
    }
}