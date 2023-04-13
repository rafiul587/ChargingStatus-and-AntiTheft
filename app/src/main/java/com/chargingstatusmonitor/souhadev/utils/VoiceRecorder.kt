package com.chargingstatusmonitor.souhadev.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import com.chargingstatusmonitor.souhadev.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class VoiceRecorder(val context: Context) {

    private var recorder: MediaRecorder? = null

    private var isRecording = false
    var audiouri: Uri? = null
    var file: ParcelFileDescriptor? = null

    private var fileName = ""

    fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        try {
            val directory: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "${Environment.DIRECTORY_MUSIC}/${context.getString(R.string.recording_folder_name)}"
            } else {
               "${Environment.getExternalStorageDirectory().absolutePath}/${context.getString(R.string.recording_folder_name)}"
            }

            fileName = getFileName()
            val values = ContentValues()
            values.put(MediaStore.Audio.Media.TITLE, fileName)
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Audio.Media.DATE_ADDED, (System.currentTimeMillis() / 1000).toInt())
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, directory)
            } else {
                val folder = File(directory)
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val file = File(folder, "$fileName.mp3")
                values.put(MediaStore.Audio.Media.DATA, file.absolutePath)
            }

            audiouri = this.context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            file = audiouri?.let { this.context.contentResolver.openFileDescriptor(it, "w") }

            file?.let {
                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setOutputFile(it.fileDescriptor)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    try {
                        prepare()
                    } catch (e: IOException) {
                        Log.e("LOG_TAG", "${e.message}")
                    }
                    start()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.printStackTrace().toString())
        }
    }

    private fun getFileName(): String {
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        val fileName = formatter.format(Date())
        return "${context.getString(R.string.record_file_prefix)}${fileName}"
    }

    private fun stopRecording() {
        recorder?.apply {
            if(isRecording) {
                stop()
            }
            release()
        }
        recorder = null
        isRecording = false
    }
}