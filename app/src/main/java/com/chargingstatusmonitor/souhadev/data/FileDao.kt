package com.chargingstatusmonitor.souhadev.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chargingstatusmonitor.souhadev.utils.FileType
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM file_entity WHERE type='${FileType.RINGTONE}'")
    fun getAllRingtones(): Flow<List<FileEntity>>

    @Query("SELECT * FROM file_entity WHERE type='${FileType.ASSET}'")
    fun getAllAssetsFiles(): Flow<List<FileEntity>>

    @Query("SELECT COUNT(type) FROM file_entity WHERE type='${FileType.RINGTONE}'")
    fun getRingtoneRowCount(): Int

    @Query("SELECT COUNT(type) FROM file_entity WHERE type='${FileType.ASSET}'")
    fun getAssetRowCount(): Int

    @Query("SELECT * FROM file_entity WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<FileEntity>

    @Query("SELECT * FROM file_entity WHERE name LIKE :query")
    fun findByName(query: String): List<FileEntity>

    @Insert
    fun insertAll(files: List<FileEntity>)

    @Query("UPDATE file_entity SET isLocked = 'false' WHERE uid=:uid")
    fun update(uid: Int): Int

    @Delete
    fun delete(user: FileEntity)

    @Query("SELECT * FROM file_entity WHERE isLocked=0")
    fun getAllUnlockFiles(): List<FileEntity>
}
