package com.example.gmaps.ndviGet;

public class Stats {
    private String ndvi;
    private String evi;
    private String evi2;
    private String nri;
    private String dswi;
    private String ndwi;

    public String getNdvi() {
        return ndvi;
    }

    public String getEvi() {
        return evi;
    }

    public String getEvi2() {
        return evi2;
    }

    public String getNri() {
        return nri;
    }

    public String getDswi() {
        return dswi;
    }

    public String getNdwi() {
        return ndwi;
    }

    public Stats(String ndvi, String evi, String evi2, String nri, String dswi, String ndwi) {
        this.ndvi = ndvi;
        this.evi = evi;
        this.evi2 = evi2;
        this.nri = nri;
        this.dswi = dswi;
        this.ndwi = ndwi;
    }
}
