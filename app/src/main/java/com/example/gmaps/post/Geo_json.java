package com.example.gmaps.post;


public class Geo_json {
    private String type;
    private Properties properties;
    private Geometry geometry;

    public String getType() {
        return type;
    }

    public Properties getProperties() {
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Geo_json(String type, Properties properties, Geometry geometry) {
        this.type = type;
        this.properties = properties;
        this.geometry = geometry;
    }
}