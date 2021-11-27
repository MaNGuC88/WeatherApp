package com.example.weatherapp.data.local.converters;

import androidx.room.TypeConverter;

import com.example.weatherapp.data.models.Coord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CoordConverter {

    @TypeConverter
    public String fromCoordToString(Coord coord) {
        if (coord == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Coord>() {}.getType();
        return gson.toJson(coord, type);
    }

    @TypeConverter
    public Coord fromStringToCoord(String coordString) {
        if (coordString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Coord>() {}.getType();
        return gson.fromJson(coordString, type);
    }

}
