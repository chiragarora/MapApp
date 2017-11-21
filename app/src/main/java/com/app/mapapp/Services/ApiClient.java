package com.app.mapapp.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 11/17/2017.
 */

public class ApiClient {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final String BASE_URL_IMAGES = "http://private-e1b8f4-getimages.apiary-mock.com/";
    private static Retrofit retrofit = null;


    public static Retrofit getClientMap(String url) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
