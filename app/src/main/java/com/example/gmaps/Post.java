package com.example.gmaps;

import com.google.gson.annotations.SerializedName;

public class Post {

    private  String userId;


    @SerializedName("body")
    private String text;

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }
}
