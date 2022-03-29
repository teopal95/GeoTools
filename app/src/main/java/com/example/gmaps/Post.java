package com.example.gmaps;

import java.util.ArrayList;

public class Post {

    private String id;

    Geo_json Geo_jsonObject;

    private String name;

    ArrayList < Object > center = new ArrayList < Object > ();

    private float area;

    private String user_id;

    public String getId() {
        return id;
    }

    public Geo_json getGeo_jsonObject() {
        return Geo_jsonObject;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

class Geo_json{
    private String type;
    Properties propertiesObject;
    Geometry geometryObject;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getPropertiesObject() {
        return propertiesObject;
    }

    public void setPropertiesObject(Properties propertiesObject) {
        this.propertiesObject = propertiesObject;
    }

    public Geometry getGeometryObject() {
        return geometryObject;
    }

    public void setGeometryObject(Geometry geometryObject) {
        this.geometryObject = geometryObject;
    }
}

class Geometry{
    private String type;
    ArrayList < Object > coordinates = new ArrayList < Object > ();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

class Properties{

}
