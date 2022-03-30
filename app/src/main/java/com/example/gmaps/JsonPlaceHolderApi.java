package com.example.gmaps;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {


    @Headers("Static-Header: 123")
    @GET("polygons")
    Call<List<Post>> getPosts(@Header("Dynamic-Header") String header, @Query("appid") String appid);

    @GET("search")
    Call<List<Image>> getImages(
            @Query("start") int start,
            @Query("end") int end,
            @Query("polyid") String polyid,
            @Query("appid") String appid );





}
