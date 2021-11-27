package com.example.weatherapp.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {

    @NonNull public final Status status;
    public final T data;
    @NonNull public final String message;

    public Resource(@NonNull Status status, T data, @NonNull String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, "");
    }

    public static <T> Resource<T> error (@NonNull String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading () {
        return new Resource<>(Status.LOADING, null, "");
    }

    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

}
