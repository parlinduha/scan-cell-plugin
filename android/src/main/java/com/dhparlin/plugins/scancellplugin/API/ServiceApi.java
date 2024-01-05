package com.dhparlin.plugins.scancellplugin.API;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceApi {

    private static final String BASE_URL = "https://api.cellmapper.net/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ApiService apiService = retrofit.create(ApiService.class);

    public static Call<ApiResponse> getFrequency(String channel, String rat) {
        return apiService.getFrequency(channel, rat);
    }
}
