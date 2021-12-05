package com.example.weatherapp.data.remote;

import com.example.weatherapp.data.models.Weather;
import com.example.weatherapp.data.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather?")
    Call<WeatherResponse> getWeathers(
            @Query("lon") Double longitude,
            @Query("lat") Double latitude,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
