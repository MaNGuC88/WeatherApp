package com.example.weatherapp.ui.weather_detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.models.Weather;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.repositories.MainRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WeatherViewModel extends ViewModel {

    private MainRepository repository;
    private String cityName;
    public WeatherResponse weatherResponse;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public LiveData<Resource<WeatherResponse>> weatherLiveData;

    @Inject
    public WeatherViewModel(MainRepository repository) {
        this.repository = repository;
    }

    public void fetchWeather() {
        repository.setCityName(cityName);
        weatherLiveData = repository.getWeather();
    }

    public WeatherResponse fetchWeatherFromDatabase() {
        weatherResponse = repository.getWeatherFromDatabase();
        return weatherResponse;
    }
}
