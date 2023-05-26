package com.chargingstatusmonitor.souhadev.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApiResponse {
    @SerializedName("result")
    @Expose
    private final List<Result> result;

    public ApiResponse(List<Result> result) {
        this.result = result;
    }

    public List<String> mapToFiles() {
        List<String>  files = new ArrayList<>();
        for (Result file: result){
            if(Objects.equals(file.getFormat(), "Animated GIF")) {
                files.add(file.getName());
            }
        }
        return  files;
    }
}