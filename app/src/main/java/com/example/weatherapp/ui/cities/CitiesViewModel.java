package com.example.weatherapp.ui.cities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.models.Weather;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.repositories.MainRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CitiesViewModel extends ViewModel {

    private MainRepository repository;
    String cityName;

    public LiveData<Resource<WeatherResponse>> weatherLiveData;

    @Inject
    public CitiesViewModel(MainRepository repository) {
        this.repository = repository;
    }

    public void fetchWeather() {
        weatherLiveData = repository.getWeather();
    }

}
