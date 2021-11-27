package com.example.weatherapp.di;

import android.content.Context;

import com.example.weatherapp.data.local.AppDatabase;
import com.example.weatherapp.data.local.WeatherDao;
import com.example.weatherapp.data.local.RoomClient;
import com.example.weatherapp.data.remote.RetrofitClient;
import com.example.weatherapp.data.remote.WeatherApi;
import com.example.weatherapp.data.repositories.MainRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)

public abstract class AppModule {

    @Provides
    public static WeatherApi provideApi() {
        return new RetrofitClient().provideApi();
    }

    @Provides
    public static MainRepository provideMainRepository(WeatherApi api, WeatherDao dao) {
        return new MainRepository(api, dao);
    }

    @Provides
    public static AppDatabase provideDatabase(@ApplicationContext Context context) {
        return new RoomClient().provideDatabase(context);
    }

    @Provides
    public static WeatherDao provideWeatherDao (AppDatabase database) {
        return database.characterDao();
    }

}
