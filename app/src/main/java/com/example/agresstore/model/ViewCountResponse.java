package com.example.agresstore.model;

import com.google.gson.annotations.SerializedName;

public class ViewCountResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("view_count")
    private int viewCount;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getMessage() {
        return message;
    }
}