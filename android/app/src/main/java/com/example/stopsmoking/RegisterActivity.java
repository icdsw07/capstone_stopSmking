package com.example.stopsmoking;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stopsmoking.retrofit.api.ApiUtils;
import com.example.stopsmoking.retrofit.api.UserClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    EditText inputId;
    EditText inputName;
    EditText inputPw;
    EditText inputPw_C;
    Button submitBtn;
    JsonObject registerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputId = findViewById(R.id.id_register);
        inputName = findViewById(R.id.name_register);
        inputPw = findViewById(R.id.pw_register);
        inputPw_C = findViewById(R.id.pw_confirm_register);
        submitBtn = findViewById(R.id.button_submit);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = inputId.getText().toString();
                String userName = inputName.getText().toString();
                String userPassword = inputPw.getText().toString();
                String userPasswordConfirm = inputPw_C.getText().toString();

                if(userId.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    inputId.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    inputName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    inputPw.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userPasswordConfirm.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    inputPw_C.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(!userPassword.equals(userPasswordConfirm)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    inputPw_C.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else{
                    registerData = new JsonObject();
                    registerData.addProperty("id",userId);
                    registerData.addProperty("password",userPassword);
                    registerData.addProperty("name",userName);
                    registerData.addProperty("yellowcard",0);
                    registerData.addProperty("userAuth",false);
                    register();

                }
            }
        });

    }

    private void register(){
        Call<JsonObject> res = ApiUtils.getUserClient().registerUser(registerData);
        res.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()){
                    Toast.makeText(getBaseContext(),"등록되었습니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    int statusCode = response.code();
                    Log.i("User_register", "onResponse: "+statusCode);
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
