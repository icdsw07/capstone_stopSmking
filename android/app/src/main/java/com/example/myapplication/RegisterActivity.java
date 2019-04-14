package com.example.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.util.https.NetRetrofit;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText passwordConfirmText = (EditText) findViewById(R.id.passwordconfirmText);

        Button registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = idText.getText().toString();
                String userName = nameText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userPasswordConfirm = passwordConfirmText.getText().toString();

                if(userId.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    idText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userName.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    nameText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    passwordText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(userPasswordConfirm.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    passwordConfirmText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else if(!userPassword.equals(userPasswordConfirm)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    passwordConfirmText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                else{
                    JsonObject registerData = new JsonObject();
                    try{
                        registerData.addProperty("id",userId);
                        registerData.addProperty("password",userPassword);
                        registerData.addProperty("name",userName);
                        registerData.addProperty("yellowcard",0);
                        registerData.addProperty("userAuth",false);

                    }catch (JsonIOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    Call<JsonObject> res = NetRetrofit.getInstance().getService().registerUser(registerData);
                    res.enqueue(new Callback<JsonObject>(){
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response){

                            if(response.isSuccessful()){
                                Toast.makeText(getBaseContext(),"등록되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                int statusCode = response.code();
                                Log.i("User_register", "onResponse: "+statusCode);
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonObject> call,Throwable t){
                            Log.e("Err", t.getMessage());
                        }
                    });

                }
            }
        });
    }
}