package com.chargingstatusmonitor.souhadev.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FileEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileEntityDao(): FileDao
}
