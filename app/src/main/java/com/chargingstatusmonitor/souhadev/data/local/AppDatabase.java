package com.chargingstatusmonitor.souhadev.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FileEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FileDao fileEntityDao();
}