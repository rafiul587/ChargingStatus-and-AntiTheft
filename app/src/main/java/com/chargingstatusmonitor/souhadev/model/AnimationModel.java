package com.chargingstatusmonitor.souhadev.model;

public class AnimationModel {
    String name;
    int progress = -1;

    public AnimationModel(String name, int progress) {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
