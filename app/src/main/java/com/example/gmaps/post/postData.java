package com.example.gmaps.post;

import com.google.gson.annotations.SerializedName;

public class postData {

@SerializedName("name")
    private String name;

@SerializedName("geo_json")
    private Gson gson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public postData(String name, Gson gson) {
        this.name = name;
        this.gson = gson;
    }
}
