package com.example.gmaps;

public class NdviGet {

    private float dt;
    private String type;
    private float dc;
    private float cl;
    Sun sunObject;
    Image ImageObject;
    Tile TileObject;
    Stats StatsObject;
    Data DataObject;

    private Image image;


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

    public Sun getSunObject() {
        return sunObject;
    }


    public Image getImage() {
        return image;
    }


    public Tile getTileObject() {
        return TileObject;
    }

    public Stats getStatsObject() {
        return StatsObject;
    }

    public Data getDataObject() {
        return DataObject;
    }

    public class Sun{
        private float elevation;
        private float azimuth;

        public float getElevation() {
            return elevation;
        }

        public float getAzimuth() {
            return azimuth;
        }
    }



    public class Data{
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
    }

    public class Stats{
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
    }

    public class Tile{
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
    }



}
