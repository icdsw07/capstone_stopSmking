package com.example.myapplication.util.https;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
//    @GET("/users/{user}")
//    Call<JsonObject> getUserData(@Path("user") String user_id);
    @POST("/users/login")
    Call<JsonObject> getUserData(@Body JsonObject login_data);

    @POST("/users")
    Call<JsonObject> registerUser(@Body JsonObject register_data);
}
