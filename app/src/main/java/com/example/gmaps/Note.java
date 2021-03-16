package com.example.gmaps;

import com.google.firebase.firestore.Exclude;

public class Note {

    private String markerId;

    private String latitude;
    private String longitude;


    public Note(){
        //public no-arg constructor needed

    }
    public Note(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;


    }

    @Exclude
    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }



    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }




}
