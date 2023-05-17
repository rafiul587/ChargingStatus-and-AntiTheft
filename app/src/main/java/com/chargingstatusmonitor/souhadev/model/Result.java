package com.chargingstatusmonitor.souhadev.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("mtime")
    @Expose
    private String mtime;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("md5")
    @Expose
    private String md5;
    @SerializedName("crc32")
    @Expose
    private String crc32;
    @SerializedName("sha1")
    @Expose
    private String sha1;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("rotation")
    @Expose
    private String rotation;
    @SerializedName("btih")
    @Expose
    private String btih;
    @SerializedName("summation")
    @Expose
    private String summation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCrc32() {
        return crc32;
    }

    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String getBtih() {
        return btih;
    }

    public void setBtih(String btih) {
        this.btih = btih;
    }

    public String getSummation() {
        return summation;
    }

    public void setSummation(String summation) {
        this.summation = summation;
    }

}

