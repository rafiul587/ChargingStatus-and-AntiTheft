package com.chargingstatusmonitor.souhadev.model;

import com.google.firebase.storage.StorageReference;

public class AnimationModel {
    StorageReference animation;
    int progress = -1;

    public AnimationModel(StorageReference animation, int progress) {
        this.animation = animation;
        this.progress = progress;
    }

    public StorageReference getAnimation() {
        return animation;
    }

    public void setAnimation(StorageReference animation) {
        this.animation = animation;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
