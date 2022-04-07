package com.example.gmaps.ndviGet;

import com.example.gmaps.ndviGet.Data;
import com.example.gmaps.ndviGet.Image;
import com.example.gmaps.ndviGet.Stats;
import com.example.gmaps.ndviGet.Sun;
import com.example.gmaps.ndviGet.Title;

public class NdviGet {

    private float dt;
    private String type;
    private float dc;
    private float cl;
    private Sun sun;
    private Stats stats;
    private Image image;
    private Title title;
    private Data data;


//GETTERS


    public float getDt() {
        return dt;
    }

    public String getType() {
        return type;
    }

    public float getDc() {
        return dc;
    }

    public float getCl() {
        return cl;
    }

    public Sun getSun() {
        return sun;
    }

    public Image getImage() {
        return image;
    }

    public Title getTitle() {
        return title;
    }

    public Data getData() {
        return data;
    }

    public Stats getStats() {
        return stats;
    }



}
