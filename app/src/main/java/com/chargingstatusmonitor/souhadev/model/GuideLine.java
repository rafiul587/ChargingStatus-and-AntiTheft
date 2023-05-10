package com.chargingstatusmonitor.souhadev.model;

import androidx.annotation.DrawableRes;

public class GuideLine {
    private int imageResId;
    private String title, description;

    public GuideLine(int imageResId, String title, String description) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
