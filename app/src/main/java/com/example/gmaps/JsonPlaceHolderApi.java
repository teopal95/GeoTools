package com.example.gmaps;

import com.example.gmaps.ndviGet.NdviGet;

import com.example.gmaps.post.Geometry;
import com.example.gmaps.post.Post;
import com.example.gmaps.post.postData;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {


    @Headers("Static-Header: 123")
    @GET("polygons")
    Call<List<Post>> getPosts(@Header("Dynamic-Header") String header, @Query("appid") String appid);

    @GET("image/search")
    Call<List<NdviGet>> getImages(
            @Query("start") int start,
            @Query("end") int end,
            @Query("polyid") String polyid,
            @Query("appid") String appid );




    @POST("polygons")
    Call<ResponseBody> createPost(
            @Body RequestBody params,
            @Query("appid") String appid);





}
