package com.example.gmaps.post;

import com.google.gson.annotations.SerializedName;

public class Properties {

@SerializedName("properties")
    String properties;

    public String getProperties() {
        return properties;
    }

    public Properties(String properties) {
        this.properties = properties;
    }
}
