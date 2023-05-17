package com.chargingstatusmonitor.souhadev.model;

import androidx.annotation.StringRes;

public class GuideLine {
    private int imageResId;
    @StringRes
    int description, title;

    public GuideLine(int imageResId, int title, int description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getTitle() {
        return title;
    }

    public int getDescription() {
        return description;
    }
}
