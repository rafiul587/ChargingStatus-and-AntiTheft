package com.chargingstatusmonitor.souhadev.utils

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.chargingstatusmonitor.souhadev.model.PreferenceModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.zip

class MyService : Service() {
    private var mReceiver: PowerConnectionReceiver? = null
    override fun onBind(p0: Intent?): IBinder? = null
    var isRegistered = false
    var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var connectFileModel: PreferenceModel
        var disconnectFileModel: PreferenceModel
        job = scope.launch {
            withContext(Dispatchers.IO) {
                applicationContext.getOnConnectFile()
                    .zip(applicationContext.getOnDisconnectFile()) { connect, disconnect ->
                        val connectSplits = connect.split("-")
                        val connectType = connectSplits[0]
                        val path = connectSplits[1]
                        val connectFile = connectSplits[2]
                        connectFileModel = PreferenceModel(connectFile, connectType, path)

                        val disconnectSplits = disconnect.split("-")
                        val disconnectType = disconnectSplits[0]
                        val disconnectPath = disconnectSplits[1]
                        val disconnectFile = disconnectSplits[2]
                        disconnectFileModel =
                            PreferenceModel(disconnectFile, disconnectType, disconnectPath)

                        Log.d("TAG", "onStartCommand: $connectFile, $disconnectFile")
                        IntentFilter().apply {
                            addAction(Intent.ACTION_POWER_CONNECTED)
                            addAction(Intent.ACTION_POWER_DISCONNECTED)
                        }.let { ifilter ->
                            try {
                                //Register or UnRegister your broadcast receiver here
                                if (isRegistered) {
                                    unregisterReceiver(mReceiver)
                                }
                                mReceiver =
                                    PowerConnectionReceiver(connectFileModel, disconnectFileModel)
                                this@MyService.registerReceiver(mReceiver, ifilter)
                                isRegistered = true
                            } catch (e: IllegalArgumentException) {
                                FirebaseCrashlytics.getInstance().log(e.printStackTrace().toString())
                            }
                        }
                    }.launchIn(this)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (mReceiver != null && isRegistered) {
            unregisterReceiver(mReceiver)
            isRegistered = false
        }
        if (job != null && job!!.isActive) {
            job!!.cancel()
        }
        super.onDestroy()
    }
}