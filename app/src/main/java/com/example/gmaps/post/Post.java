package com.example.gmaps.post;


import java.util.ArrayList;

public class Post {

    private String id;


    private Geo_json Geo_json;




    private String name;
    ArrayList<Object> center = new ArrayList<Object>();
    private float area;
    private String user_id;


    public String getId() {
        return id;
    }

    public com.example.gmaps.post.Geo_json getGeo_json() {
        return Geo_json;
    }



    public String getName() {
        return name;
    }

    public ArrayList<Object> getCenter() {
        return center;
    }

    public float getArea() {
        return area;
    }

    public String getUser_id() {
        return user_id;
    }
}
