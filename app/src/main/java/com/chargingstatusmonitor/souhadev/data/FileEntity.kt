package com.chargingstatusmonitor.souhadev.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "file_entity")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String = "",
    val type: String = "",
    val uri: String = "",
    val duration: String = "",
    val isLocked: Boolean = true
)
