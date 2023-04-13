package com.chargingstatusmonitor.souhadev.utils

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chargingstatusmonitor.souhadev.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
private val CONNECT_FILE = stringPreferencesKey("connect_file")
private val DISCONNECT_FILE = stringPreferencesKey("disconnect_file")

fun Context.getOnConnectFile(): Flow<String> {
    return dataStore.data
        .map { preferences ->
           // preferences[CONNECT_FILE] ?: "${FileType.ASSET}-asset-thank.mp3"
            preferences[CONNECT_FILE] ?: "${FileType.ASSET}-asset-"+getResources().getString(R.string.mp3_country)+".mp3"
        }
}

suspend fun Context.saveOnConnectFile(value: String): Preferences {
    return dataStore.edit {
        it.set(
            CONNECT_FILE,
            value
        )
    }
}

suspend fun Context.saveOnDisconnectFile(value: String): Preferences {
    return dataStore.edit {
        it.set(
            DISCONNECT_FILE,
            value
        )
    }
}

fun Context.getOnDisconnectFile(): Flow<String> {
    return dataStore.data
        .map { preferences ->
            //preferences[DISCONNECT_FILE] ?: "${FileType.ASSET}-asset-v1.mp3"
            preferences[DISCONNECT_FILE] ?: "${FileType.ASSET}-asset-"+getResources().getString(R.string.mp3_disconnect_country)+".mp3"
        }
}