package com.example.stopsmoking.retrofit.api;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface UserClient {
    final String Base_URL = "http://10.210.32.31:80/";
    @GET("/users/{user}")
    Call<JsonObject> searchUser(@Path("user") String user_id);

    @Streaming
    @GET("/users/face/{user}")
    Call<ResponseBody> downloadFaceFeature(@Path("user") String user_id);
    @POST("/users/login")
    Call<JsonObject> getUserData(@Body JsonObject login_data);

    @POST("/users")
    Call<JsonObject> registerUser(@Body JsonObject register_data);

    @PATCH("/users")
    Call<JsonObject> updateUserAuth(@Body JsonObject user_id);

    @Multipart
    @POST("/users/face")
    Call<JsonObject> uploadFaceFeature(@Part MultipartBody.Part file, @Part("name")RequestBody description);

    @POST("/measurelogs")
    Call<JsonObject> measureLogging(@Body JsonObject measure_data);

    @GET("/measurelogs/{user}")
    Call<List<JsonObject>> getLogs(@Path("user") String user_id);
}
