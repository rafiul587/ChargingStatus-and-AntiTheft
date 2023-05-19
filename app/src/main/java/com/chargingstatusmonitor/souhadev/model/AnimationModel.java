package com.chargingstatusmonitor.souhadev.model;

public class AnimationModel {
    String name;
    int progress = -1;
    boolean isSelected = false;

    public AnimationModel(String name, boolean isSelected, int progress) {
        this.name = name;
        this.progress = progress;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
