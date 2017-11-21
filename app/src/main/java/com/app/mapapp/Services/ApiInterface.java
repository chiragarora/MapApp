package com.app.mapapp.Services;

import com.app.mapapp.Models.ImagesList;
import com.app.mapapp.Models.PlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by user on 11/17/2017.
 */

public interface ApiInterface {
    @GET("nearbysearch/json")
    Call<PlacesResponse> getMap(@Query("location") String location, @Query("radius") long radius,
                                @Query("type") String type, @Query("sensor") boolean sensor, @Query("key") String key);

    @GET("getImages")
    Call<ImagesList> getImages();

}
