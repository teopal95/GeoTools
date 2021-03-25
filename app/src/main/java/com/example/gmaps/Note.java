package com.example.gmaps;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

import java.util.List;

public class Note {



    private String markerId;



    List<String> tags;


    public Note() {
        //public no-arg constructor needed

    }


    public Note(List<String> tags) {

        this.tags = tags;


    }

    @Exclude
    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }


    public List<String> getTags() {
        return tags;
    }



}
