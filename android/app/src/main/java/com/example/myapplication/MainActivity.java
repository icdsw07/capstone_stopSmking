package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Data.UserData;
import com.example.myapplication.util.https.NetRetrofit;
import com.example.myapplication.util.https.RetrofitService;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText idText;
    EditText passwordText;
    @Nullable
    private OnSearchCallbacks callbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        TextView registerButton = (TextView) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(RegisterIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                onSearch(new OnSearchCallbacks() {
                    @Override
                    public void onSuccess(@NonNull UserData user) {

                        Intent userHomeIntent = new Intent(MainActivity.this, UserHomeActivity.class);
                        userHomeIntent.putExtra("OBJECT",user);
                        MainActivity.this.startActivity(userHomeIntent);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        // here you access the throwable and check what to do
                    }
                });


            }
        });

    }

    public void onSearch(@Nullable final OnSearchCallbacks callbacks) {
        final JsonObject loginData = new JsonObject();
        String id_input= idText.getText().toString();
        String passWord_input = passwordText.getText().toString();
        loginData.addProperty("id",id_input);
        loginData.addProperty("password",passWord_input);
        if (!id_input.isEmpty()) {
            Call<JsonObject> res = NetRetrofit.getInstance().getService().getUserData(loginData);

            res.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body().size() != 1){
                        Log.d("server", "hihi");
                        UserData user = new UserData();
                        user.setUserId(response.body().get("id").toString());
                        user.setName(response.body().get("name").toString());
                        user.setYellowCard(response.body().get("yellowcard").getAsInt());
                        user.setUserAuth(response.body().get("userAuth").getAsBoolean());

                        if (callbacks != null)
                            callbacks.onSuccess(user);
                    }
                    else{
                        Toast.makeText(getBaseContext(), "비밀번호가 틀립니다..", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("Err", t.getMessage());
                    if (callbacks != null)
                        callbacks.onError(t);
                }
            });
        } else Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();


    }
}
