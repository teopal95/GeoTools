package com.example.gmaps;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {


    @Headers("Static-Header: 123")
    @GET("polygons")
    Call<List<Post>> getPosts(@Header("Dynamic-Header") String header, @Query("appid") String appid);




}
