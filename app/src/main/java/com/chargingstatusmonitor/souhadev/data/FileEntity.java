package com.chargingstatusmonitor.souhadev.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "file_entity")
public class FileEntity {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String name;

    private String type;

    private String uri;

    private String duration;

    private boolean isLocked;
    @Ignore
    private boolean isPlaying = false;

    public FileEntity(String name, String type, String uri, String duration, boolean isLocked) {
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.duration = duration;
        this.isLocked = isLocked;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
