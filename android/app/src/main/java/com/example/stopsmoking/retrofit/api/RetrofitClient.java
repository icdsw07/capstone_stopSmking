package com.example.stopsmoking.retrofit.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(40,TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
