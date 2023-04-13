package com.chargingstatusmonitor.souhadev.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.model.PreferenceModel


class PowerConnectionReceiver(
    private val connectModel: PreferenceModel,
    private val disconnectModel: PreferenceModel,
) : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, batteryStatus: Intent) {

        if (mediaPlayer != null) {
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        when (batteryStatus.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                try {
                    if (connectModel.type == FileType.ASSET) {
                        val descriptor = context.assets.openFd("${context.getString(R.string.asset_folder_name)}/${connectModel.name}")
                        mediaPlayer = MediaPlayer()
                        Log.d(
                            "TAG",
                            "onReceive: ${descriptor.fileDescriptor}, ${descriptor.startOffset}, ${descriptor.length}"
                        )
                        mediaPlayer!!.setDataSource(
                            descriptor.fileDescriptor,
                            descriptor.startOffset,
                            descriptor.length
                        )
                        descriptor.close()
                        mediaPlayer!!.prepare()
                        mediaPlayer!!.start()
                    } else {
                        mediaPlayer = MediaPlayer()
                        mediaPlayer!!.setDataSource(context, Uri.parse(connectModel.path))
                        mediaPlayer!!.prepare()
                        mediaPlayer!!.start()
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "onReceive: ${e.message}")
                }

            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                try {
                    if (disconnectModel.type == FileType.ASSET) {
                        val descriptor = context.assets.openFd("${context.getString(R.string.asset_folder_name)}/${disconnectModel.name}")
                        mediaPlayer = MediaPlayer()
                        mediaPlayer!!.setDataSource(
                            descriptor.fileDescriptor,
                            descriptor.startOffset,
                            descriptor.length
                        )
                        descriptor.close()
                        mediaPlayer!!.prepare()
                        mediaPlayer!!.start()
                    } else {
                        mediaPlayer = MediaPlayer()
                        mediaPlayer!!.setDataSource(context, Uri.parse(disconnectModel.path))
                        mediaPlayer!!.prepare()
                        mediaPlayer!!.start()
                    }
                } catch (e: Exception) {

                }
            }
        }
    }
}