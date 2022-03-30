package com.example.gmaps;

import java.util.ArrayList;

public class Post {

    private String id;
    Geo_json Geo_jsonObject;
    private String name;
    ArrayList<Object> center = new ArrayList<Object>();
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


    public float getArea() {
        return area;
    }


    public String getUser_id() {
        return user_id;
    }


    class Geo_json {
        private String type;
        Properties propertiesObject;
        Geometry geometryObject;

        public String getType() {
            return type;
        }

        public Properties getPropertiesObject() {
            return propertiesObject;
        }

        public Geometry getGeometryObject() {
            return geometryObject;
        }

    }

    class Geometry {
        private String type;
        ArrayList<Object> coordinates = new ArrayList<Object>();

        public String getType() {
            return type;
        }

    }

    class Properties {

    }

    public Post(Geo_json geo_jsonObject, String name) {
        Geo_jsonObject = geo_jsonObject;
        this.name = name;
    }
}
