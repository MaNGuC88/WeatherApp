package com.example.weatherapp.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.weatherapp.data.local.converters.CloudsConverter;
import com.example.weatherapp.data.local.converters.CoordConverter;
import com.example.weatherapp.data.local.converters.MainConverter;
import com.example.weatherapp.data.local.converters.SysConverter;
import com.example.weatherapp.data.local.converters.WeatherListConverter;
import com.example.weatherapp.data.local.converters.WindConverter;
import com.example.weatherapp.data.models.WeatherResponse;

@Database(entities = {WeatherResponse.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WeatherDao characterDao();

}
