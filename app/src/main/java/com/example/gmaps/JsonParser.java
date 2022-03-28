package com.example.gmaps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {


    private final static String TAG = "JsonParser";


    public static JSONArray parseResponse(String data){
        JSONArray jsondata = new JSONArray();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length() - 1; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", jsonArray.getJSONObject(i).getString("name"));
                jsonObject.put("id", jsonArray.getJSONObject(i).getString("id"));
                jsonObject.put("center", jsonArray.getJSONObject(i).getJSONArray("center"));
                jsondata.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "  ID EINAI " + jsondata);
        return jsondata;
    }


    public static String getId(String name, JSONArray jsonArray){
        String id = null;
        for(int i=0;i<jsonArray.length()-1;i++){
            try {
                if(name.equals(jsonArray.getJSONObject(i).getString("name"))){
                    id = jsonArray.getJSONObject(i).getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return id;
    }


    public static String getImage(String data) {
        String ndvi = null;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObject image = jsonObject.getJSONObject("image");
            ndvi = image.getString("ndvi");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ndvi;
    }
}
