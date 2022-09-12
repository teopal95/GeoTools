package com.example.gmaps.post;


import java.util.ArrayList;

public class Post {



    private String id;

    private Gson gson;

    private String name;

    private ArrayList center;

    private double area;

    private String user_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getCenter() {
        return center;
    }

    public void setCenter(ArrayList center) {
        this.center = center;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Post(String id, Gson gson, String name, ArrayList center, double area, String user_id) {
        this.id = id;
        this.gson = gson;
        this.name = name;
        this.center = center;
        this.area = area;
        this.user_id = user_id;
    }
}
