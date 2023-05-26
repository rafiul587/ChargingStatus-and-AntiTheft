package com.chargingstatusmonitor.souhadev.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.chargingstatusmonitor.souhadev.utils.FileType;

import java.util.List;

@Dao
public interface FileDao {
    @Query("SELECT * FROM file_entity WHERE type='" + FileType.RINGTONE + "'")
    LiveData<List<FileEntity>> getAllRingtones();

    @Query("SELECT * FROM file_entity WHERE type='" + FileType.ASSET + "'")
    LiveData<List<FileEntity>> getAllAssetsFiles();

    @Query("SELECT COUNT(type) FROM file_entity WHERE type='" + FileType.RINGTONE + "'")
    int getRingtoneRowCount();

    @Query("SELECT COUNT(type) FROM file_entity WHERE type='" + FileType.ASSET + "'")
    int getAssetRowCount();

    @Query("SELECT * FROM file_entity WHERE name LIKE :query")
    List<FileEntity> findByName(String query);

    @Insert
    void insertAll(List<FileEntity> files);

    @Query("UPDATE file_entity SET isLocked = 'false' WHERE uid=:uid")
    int update(int uid);
}
