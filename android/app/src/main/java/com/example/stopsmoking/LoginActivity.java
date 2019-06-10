package com.example.stopsmoking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stopsmoking.cvlibrary.Helpers.FileHelper;
import com.example.stopsmoking.data.UserData;
import com.example.stopsmoking.retrofit.api.ApiUtils;
import com.example.stopsmoking.retrofit.api.UserClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    EditText inputId;
    EditText inputPw;
    Button loginBtn;
    Button registerBtn;
    boolean loginFlag;
    UserData user;
    JsonObject loginData;
    Button adminBtn;
    ProgressDialog dialog;
    private UserClient service;
    private FileHelper fh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        service = ApiUtils.getUserClient();

        user = new UserData();
        inputId = (EditText) findViewById(R.id.id_input_login);
        inputPw = (EditText) findViewById(R.id.pw_input_login);
        loginBtn = (Button) findViewById(R.id.button_login);
        registerBtn = (Button) findViewById(R.id.button_register);
        adminBtn =(Button) findViewById(R.id.admin);

        loginData = new JsonObject();

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AdminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(AdminIntent);

            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
                loginRequest();

                if(loginFlag){
                    if(user.isUserAuth()){
                        downloadFile();
//                        LoginIntent.putExtra("OBJECT", user);
//                        LoginActivity.this.startActivity(LoginIntent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "안면데이터 등록 후 사용하세요.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(RegisterIntent);
            }
        });

    }

    private boolean writeResponseBodyToDisk(ResponseBody body){
        try{
            fh = new FileHelper();
            File faceFeature = new File(fh.TEST_PATH + user.getUserId()+".xml");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try{
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(faceFeature);
                while (true){
                    int read = inputStream.read(fileReader);
                    if(read == -1){
                        break;
                    }
                    outputStream.write(fileReader,0,read);
                    fileSizeDownloaded += read;
                    Log.d("Login", "fileDownload: "+ fileSizeDownloaded +" of "+ fileSize);

                }
                outputStream.flush();
                return true;
            }catch (IOException e){
                return false;
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }
        }catch (IOException e){
            return false;
        }
    }

    private void downloadFile(){
        Call<ResponseBody> res = ApiUtils.getUserClient().downloadFaceFeature(user.getUserId());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if(response.isSuccessful()){

//                    Toast.makeText(LoginActivity.this, "download was succesful?"+ success, Toast.LENGTH_SHORT).show();
                    faceDownTask faceDownTask = new faceDownTask();
                    faceDownTask.execute(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("facedown", "onFailure: "+ t);
                Toast.makeText(LoginActivity.this, "faile"+t, Toast.LENGTH_SHORT).show();
            }
        });



    }

    class faceDownTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("잠시만 기다려 주세요.");
            dialog.show();
        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            boolean success = writeResponseBodyToDisk(urls[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginIntent.putExtra("OBJECT", user);
            LoginActivity.this.startActivity(LoginIntent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginFlag = false;
    }


    private void loginRequest(){
        loginData.addProperty("id",inputId.getText().toString());
        loginData.addProperty("password",inputPw.getText().toString());

        if(inputId.getText().toString().isEmpty()){
            Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(inputPw.getText().toString().isEmpty()){
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            Call<JsonObject> res = ApiUtils.getUserClient().getUserData(loginData);
            res.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body().size() != 1){
                        user.setUserId(response.body().get("id").toString().replace("\"", ""));
                        user.setName(response.body().get("name").toString().replace("\"", ""));
                        user.setYellowCard(response.body().get("yellowcard").getAsInt());
                        user.setUserAuth(response.body().get("userAuth").getAsBoolean());
                        user.setRegisteredDate(response.body().get("createdAt").getAsString());
                        loginFlag=true;
                    }
                    else{
                        Toast.makeText(getBaseContext(), "비밀번호가 틀립니다..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("login", t.getMessage());
//                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
