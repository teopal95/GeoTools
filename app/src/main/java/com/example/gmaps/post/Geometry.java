package com.example.gmaps.post;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Geometry {

    private String type;

    private ArrayList<LatLng> coordinates;

    public Geometry(String type, List<LatLng> coordinates) {
        this.type = type;
        this.coordinates = (ArrayList<LatLng>) coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<LatLng> coordinates) {
        this.coordinates = coordinates;
    }
}
