package com.example.gmaps.post;

import java.util.ArrayList;

public class Geometry {
    private String type;
    ArrayList<Object> coordinates = new ArrayList<Object>();

    public String getType() {
        return type;
    }

    public Geometry(String type, ArrayList<Object> coordinates) {
        this.type = type;
        this.coordinates = coordinates;

    }

}
