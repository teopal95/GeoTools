package com.example.gmaps.ndviGet;

public class Sun {
    private float elevation;
    private float azimuth;

    public float getElevation() {
        return elevation;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public Sun(float elevation, float azimuth) {
        this.elevation = elevation;
        this.azimuth = azimuth;
    }
}
