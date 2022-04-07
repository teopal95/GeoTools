package com.example.gmaps.post;

import java.util.ArrayList;
import java.util.List;

public class Geometry {

    private String type;

    private ArrayList<List> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<List> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<List> coordinates) {
        this.coordinates = coordinates;
    }
}
