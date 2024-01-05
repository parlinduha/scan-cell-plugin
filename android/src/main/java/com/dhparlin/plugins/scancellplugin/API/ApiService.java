package com.dhparlin.plugins.scancellplugin.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v6/getFrequency")
    Call<ApiResponse> getFrequency(
            @Query("Channel") String channel,
            @Query("RAT") String rat
    );
}