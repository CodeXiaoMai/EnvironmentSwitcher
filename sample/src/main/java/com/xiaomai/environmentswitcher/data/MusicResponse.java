package com.xiaomai.environmentswitcher.data;

import com.google.gson.annotations.SerializedName;

public class MusicResponse {
    @SerializedName("value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MusicResponse{" +
                "value='" + value + '\'' +
                '}';
    }
}