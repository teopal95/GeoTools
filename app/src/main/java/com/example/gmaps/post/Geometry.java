package com.example.gmaps.post;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Geometry {
    public Geometry(){

    }
    @SerializedName("type")
    private String type;

    @SerializedName("coordinates")
    private String coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Geometry(String type, String coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }
}
