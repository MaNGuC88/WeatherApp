package com.example.weatherapp.data.local.converters;

import androidx.room.TypeConverter;

import com.example.weatherapp.data.models.Clouds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CloudsConverter {

    @TypeConverter
    public String fromCloudsToString(Clouds clouds) {
        if (clouds == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Clouds>() {}.getType();
        return gson.toJson(clouds, type);
    }

    @TypeConverter
    public Clouds fromStringToClouds(String cloudsString) {
        if (cloudsString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Clouds>() {}.getType();
        return gson.fromJson(cloudsString, type);
    }

}
