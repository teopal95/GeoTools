package com.example.gmaps.ndviGet;

public class Data {
    private String truecolor;
    private String falsecolor;
    private String ndvi;
    private String evi;
    private String evi2;
    private String nri;
    private String dswi;
    private String ndwi;

    public String getTruecolor() {
        return truecolor;
    }

    public String getFalsecolor() {
        return falsecolor;
    }

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

    public Data(String truecolor, String falsecolor, String ndvi, String evi, String evi2, String nri, String dswi, String ndwi) {
        this.truecolor = truecolor;
        this.falsecolor = falsecolor;
        this.ndvi = ndvi;
        this.evi = evi;
        this.evi2 = evi2;
        this.nri = nri;
        this.dswi = dswi;
        this.ndwi = ndwi;
    }
}
