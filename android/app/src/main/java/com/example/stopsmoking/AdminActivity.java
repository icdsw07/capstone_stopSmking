package com.example.stopsmoking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stopsmoking.cvlibrary.Helpers.FileHelper;
import com.example.stopsmoking.data.UserData;
import com.example.stopsmoking.retrofit.api.ApiUtils;
import com.example.stopsmoking.retrofit.api.RetrofitClient;
import com.example.stopsmoking.retrofit.api.UserClient;
import com.google.gson.JsonObject;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import org.opencv.ml.TrainData;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminActivity extends AppCompatActivity {
    private static final String    TAG                 = "FaceTraining";
    Button searchButton;
    Button registerButton;
    TextView idText;
    View dialogView;
    boolean searchFlag;
    UserData user;
    TrainTask train;
    RequestTask request;
    ProgressDialog dialog;
    private FileHelper fh;
    String name ;
    String id;
    JsonObject faceFeature;
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Cannot load OpenCV library");
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.loadLibrary("opencv_java4");
                    Log.d(TAG, "onManagerConnected: Create SVM");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("사용자 등록");

        searchButton = (Button)findViewById(R.id.searchButton);

        idText = (TextView)findViewById(R.id.idText);
        user = new UserData();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSearch();
                if(searchFlag){
                    name = user.getName();
                    id = user.getUserId();
                    dialogView = (View) View.inflate(AdminActivity.this, R.layout.dialog_search, null);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(AdminActivity.this);
                    dlg.setTitle("사용자 정보");
                    dlg.setIcon(R.drawable.ic_menu_person_black);
                    dlg.setView(dialogView);
                    TextView admin_idText = dialogView.findViewById(R.id.admin_idText);
                    TextView nameText = dialogView.findViewById(R.id.admin_nameText);
                    TextView authText = dialogView.findViewById(R.id.admin_authText);
                    admin_idText.setText(user.getUserId().replace("\"",""));
                    nameText.setText(user.getName().replace("\"",""));
                    dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AdminActivity.this, AddFaceActivity.class);
                            Log.d(TAG, "userTest: "+ user.getName());
                            intent.putExtra("OBJECT", user);
                            startActivityForResult(intent,3000);
                        }
                    });

                    if(user.isUserAuth()){ //인증 된상태
                        authText.setText("인증됨");
                    }
                    else{ // 인증안된상태
                        authText.setText("인증 안됨");
                    }
                    dlg.show();
                }

            }
        });

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent registerIntent = new Intent(AdminActivity.this, AddFaceActivity.class);
//                startActivityForResult(registerIntent,3000);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            name = user.getName();
            id = user.getUserId();
            Log.d(TAG, "onActivityResult: "+name+"---"+id);
//            Log.d(TAG, "onActivityResult: ResultOK");
//            if (!OpenCVLoader.initDebug()) {
//                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
////                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
//
//            } else {
//                Log.d(TAG, "OpenCV library found inside package. Using it!");
//                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//            }


            train = new TrainTask();
            Log.d(TAG, "onActivityResult: TrainTask");
            train.execute();
        }
    }

    private double [] hogImg(Mat img){
        HOGDescriptor hog = new HOGDescriptor(
                new Size(24, 24), //winSize
                new Size(12, 12), //blocksize
                new Size(6, 6), //blockStride,
                new Size(12, 12), //cellSize,
                9); //nbins
        MatOfFloat descriptors = new MatOfFloat();
        hog.compute(img, descriptors);

        float[] descArr = descriptors.toArray();
        double retArr[] = new double[descArr.length];
        for (int i = 0; i < descArr.length; i++) {
            retArr[i] = descArr[i];
        }
        return retArr;
    }

    class TrainTask extends AsyncTask{
        @Override
        protected void onPreExecute() { //트레이닝 진행전 ..

            Log.d(TAG, "onPreExecute: Before Training");
//            dialog = new ProgressDialog(getApplicationContext());
            dialog = new ProgressDialog(AdminActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("안면 데이터를 등록하는 중입니다.");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) { // 트레이닝 진행
            List<Mat> images = new ArrayList<>();
            fh = new FileHelper();
            String wholeFolderPath = fh.TEST_PATH + name;
            final File[] persons = fh.getTestList();
            if(persons.length > 0){
                Log.d(TAG, "person length"+Integer.toString(persons.length));
                for(File person : persons){
                    if(person.isDirectory()){

                        if(!person.getName().equals(user.getUserId())) continue;
                        File[] files = person.listFiles();
                        Log.d(TAG, "file Length"+Integer.toString(files.length));
                        Log.d(TAG, "Passed Directory name: "+person.getName());
                        for(File file :files){

                            if(fh.isFileAnImage(file)){
                                Mat imgRgb = Imgcodecs.imread(file.getAbsolutePath());
                                Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2GRAY);
                                Mat processedImage = new Mat();
                                imgRgb.copyTo(processedImage);
                                images.add(processedImage);
//
                            }
                        }
                    }
                }

            }

            int image_cnt = images.size();
            int trainArea = images.get(0).rows() * images.get(0).cols();
            Mat trainingData = new Mat(image_cnt,trainArea, CvType.CV_32FC1);
            Mat labelsMat = new Mat(image_cnt,1,CvType.CV_32SC1);
            for (int i = 0 ; i<image_cnt;i++){
                int [] temp = new int[1];
                temp[0]=0;
                labelsMat.put(i,0,temp);
            }

            int hogArea = hogImg(images.get(0)).length;
            Mat hogData = new Mat(image_cnt, hogArea, CvType.CV_32FC1);
            for(int i = 0 ; i<image_cnt; i++){
                double[] hog_img = hogImg(images.get(i));
                for(int j = 0 ; j<hogArea; j++){
                    double[] temp = new double[1];
                    temp[0] = hog_img[j];
                    hogData.put(i,j,temp);
                }
            }

            int counter;
            for(int i = 0 ; i<image_cnt ; i++){
                counter = 0 ;
                for (int rows = 0 ; rows<images.get(0).rows(); rows++){
                    for(int cols = 0 ; cols<images.get(0).cols(); cols++){
                        trainingData.put(i,counter,images.get(i).get(rows,cols));
                        counter++;
                    }
                }
            }

            SVM svm = SVM.create();
//            svm.setType(SVM.C_SVC);
            svm.setType(SVM.ONE_CLASS);
            svm.setKernel(SVM.LINEAR);
//            svm.setKernel(SVM.RBF);
//            svm.setDegree(3);
            svm.setGamma(1);
//            svm.setCoef0(0);
            svm.setC(1);
//            svm.setP(0);
            svm.setNu(0.1);
            svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 4000, 1e-6));
//            svm.train(trainingData, Ml.ROW_SAMPLE, labelsMat);
            svm.train(hogData, Ml.ROW_SAMPLE, labelsMat);
            Log.d(TAG, "train done!");
            svm.save(fh.TEST_PATH+id+".xml");
            Log.d(TAG, "save done!");


            return null;
        }

        @Override
        protected void onPostExecute(Object o) { // 트레이닝 끝나고 디비에 업로드
            super.onPostExecute(o);

            request = new RequestTask();  //디비 업로드하는 asynctask
            request.execute();


        }
    }


    class RequestTask extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

//            String str = null; // str is trained model data (xml)
//            String face_xml = null;
//            try {
//                InputStream in = new FileInputStream(fh.TEST_PATH+"train.xml");
//                BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
//
//                StringBuilder sb = new StringBuilder();
//                while((str = r.readLine()) != null){
//                    sb.append(str);
//
//                }
//                face_xml = sb.toString();
////                Log.d(TAG, "xml: " + face_xml);
//                Log.d(TAG, "xml Size: "+ sb.length());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//            faceFeature = new JsonObject();
//            faceFeature.addProperty("id", "dbtest");
//            faceFeature.addProperty("faceFeature", face_xml);

//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .writeTimeout(120, TimeUnit.SECONDS)
//                    .readTimeout(120,TimeUnit.SECONDS)
//                    .connectTimeout(60,TimeUnit.SECONDS)
//                    .build();

            File faceFeature = new File(fh.TEST_PATH+id+".xml");
            RequestBody faceBody = RequestBody.create(MediaType.parse("text/xml"),faceFeature);
            MultipartBody.Part body = MultipartBody.Part.createFormData("faceFeature",faceFeature.getName(),faceBody);
            String descriptionString = "android";
            RequestBody description = RequestBody.create(MediaType.parse("Multipart/form-data"), descriptionString);

            Call<JsonObject> res = ApiUtils.getUserClient().uploadFaceFeature(body,description);
            res.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if(response.isSuccessful()){

                        String getResult = response.body().get("result").toString().replace("\"","");

                        if (getResult.equals("success")){
                            Log.d(TAG, "json: "+getResult);
                            JsonObject user_id = new JsonObject();
                            user_id.addProperty("id",id);
                            Call<JsonObject> re = ApiUtils.getUserClient().updateUserAuth(user_id);
                            re.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response2) {
                                    String getResult2 = response2.body().get("result").toString().replace("\"","");
                                    if (getResult2.equals("success")) {
                                        Log.d(TAG, "json: " + getResult2);
                                        dialog.dismiss();
                                        Toast.makeText(AdminActivity.this,"안면 데이터 업로드가 완료 되었습니다!!",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        dialog.dismiss();
                                        Toast.makeText(AdminActivity.this,"userAuth 업데이트 실패...",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    dialog.dismiss();
                                    Toast.makeText(AdminActivity.this,"response: userAuth 업데이트 실패...",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                    else{
                        dialog.dismiss();
                        Toast.makeText(AdminActivity.this,"안면 데이터 업로드 실패...",Toast.LENGTH_SHORT).show();
                    }
//                    if(response.body().get("result").getAsBoolean()){
////                        Toast.makeText(getBaseContext(),"데이터베이스 업로드 완료.",Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "FaceFeature DB upload: Success");
//                        dialog.dismiss();
//                        Toast.makeText(AdminActivity.this,"디비 업로드가 완료 되었습니다.",Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Log.d(TAG, "FaceFeature DB upload: Fail!!!!!!!!!");
//                    }

                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d(TAG, "FaceFeature DB Fail: "+ t);
                    dialog.dismiss();
                    Toast.makeText(AdminActivity.this,"response fail!!!",Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        searchFlag=false;
    }

    public void onSearch(){

        String userId = idText.getText().toString();
        if(!userId.isEmpty()){
            Call<JsonObject> res = ApiUtils.getUserClient().searchUser(userId);
            res.enqueue(new Callback<JsonObject>(){
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body().size() != 1) {

                        user.setUserId(response.body().get("id").toString().replace("\"",""));
                        user.setName(response.body().get("name").toString().replace("\"",""));
                        user.setUserAuth(response.body().get("userAuth").getAsBoolean());
                        searchFlag=true;


                    }
                    else{
                        Toast.makeText(getBaseContext(), "가입된 아이디가 존재하지 않습니다...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("Err", t.getMessage());
                }
            });
        }
        else{
            Toast.makeText(AdminActivity.this, "ID를 입력하세요. ", Toast.LENGTH_SHORT).show();
            idText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}