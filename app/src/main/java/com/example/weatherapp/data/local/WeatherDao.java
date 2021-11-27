package com.example.weatherapp.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weatherapp.data.models.WeatherResponse;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeather(WeatherResponse weatherResponse);

    @Query("SELECT * FROM weatherresponse")
    WeatherResponse getWeather();

}
