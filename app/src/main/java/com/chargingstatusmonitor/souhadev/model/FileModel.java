package com.chargingstatusmonitor.souhadev.model;

public class FileModel {
    private String id;
    private String name;
    private String duration;
    private String path;

    public FileModel(String id, String name, String duration, String path) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}