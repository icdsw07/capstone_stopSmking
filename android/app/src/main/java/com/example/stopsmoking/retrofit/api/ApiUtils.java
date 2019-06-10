package com.example.stopsmoking.retrofit.api;

public class ApiUtils {
    public static final String BASE_URL = "http://52.79.121.199:80/";

    public static UserClient getUserClient() {
        return RetrofitClient.getClient(BASE_URL).create(UserClient.class);
    }
}
