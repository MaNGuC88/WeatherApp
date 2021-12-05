package com.example.weatherapp.ui.weather_detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.repositories.MainRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WeatherViewModel extends ViewModel {

    private MainRepository repository;
    public WeatherResponse weatherResponse;
    private Double longitude;
    private Double latitude;

    public void setCoordinates(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LiveData<Resource<WeatherResponse>> weatherLiveData;

    @Inject
    public WeatherViewModel(MainRepository repository) {
        this.repository = repository;
    }

    public void fetchWeather() {
        repository.setCoordinates(longitude, latitude);
        weatherLiveData = repository.getWeather();
    }

    public WeatherResponse fetchWeatherFromDB() {
        weatherResponse = repository.getWeatherFromDatabase();
        return weatherResponse;
    }
}
