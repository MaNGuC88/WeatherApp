package com.example.weatherapp.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.App;
import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.local.WeatherDao;
import com.example.weatherapp.data.models.Weather;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.remote.WeatherApi;
import com.example.weatherapp.ui.weather_detail.WeatherViewModel;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRepository {

    private WeatherApi api;
    private WeatherDao dao;
    private Double longitude;
    private Double latitude;

    @Inject
    public MainRepository(WeatherApi api, WeatherDao dao) {
        this.api = api;
        this.dao = dao;
    }

    public void setCoordinates(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MutableLiveData<Resource<WeatherResponse>> getWeather() {
        MutableLiveData<Resource<WeatherResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());
        api.getWeathers(longitude, latitude,"metric", "1db948b82d5438ce0a50de69cba2b36c")
                .enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                    dao.insertWeather(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                liveData.setValue(Resource.error(t.getLocalizedMessage(),null));
            }
        });
        return liveData;
    }

    public WeatherResponse getWeatherFromDatabase() {
        return dao.getWeather();
    }

}
