package com.example.stopsmoking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.stopsmoking.cvlibrary.Helpers.FileHelper;
import com.example.stopsmoking.data.Box;
import com.example.stopsmoking.data.UserData;
import com.example.stopsmoking.retrofit.api.ApiUtils;
import com.example.stopsmoking.retrofit.api.UserClient;
import com.google.gson.JsonObject;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MeasureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private BluetoothSPP bt;

    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    private Box box;
    private FileHelper fh;
    private SVM svm;
    private boolean authFlag = false;
    private int violenceNum = 0;
    private int nothingNum = 0;
    private View rootView;
    ProgressDialog dialog;
    UserData user;
    MeasureTask measureTask;
    LogUploadTask logUploadTask;
    private List<Integer> COvalues = new ArrayList<>();
//    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName);
    public native Box detect(long cascadeClassifier_face, long matAddrInput, long matAddrResult);
    public long cascadeClassifier_face = 0;


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }
    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
//        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
//        Log.d(TAG, "read_cascade_file:");
//
//        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


//    private TextureView mCameraTextureView;
//    private Preview mPreview;
//    private static final String TAG ="MeasureActivity";
//    static final int REQUEST_CAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentData = getIntent();
        user = (UserData) intentData.getParcelableExtra("OBJECT");
        rootView = findViewById(R.id.activity_surface_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_measure);



        svm = SVM.load(fh.TEST_PATH+user.getUserId()+".xml");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else read_cascade_file();
        }
        else  read_cascade_file();

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setMaxFrameSize(800,800);
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);


        bt= ((ConnectActivity)ConnectActivity.mContext).bt;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        //블루투스 스레드 종료 하는것
        if (measureTask != null){
            measureTask.cancel(true);
            bt.stopService();
        }

        if(logUploadTask != null)
            logUploadTask.cancel(true);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        float warning=0;
        matInput = inputFrame.rgba();
//        Mat mat_copy = new Mat();
//        matInput.copyTo(mat_copy);
        Log.d(TAG, "row" + matInput.rows());
        Log.d(TAG, "col" + matInput.cols());

        if ( matResult == null )
            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

//        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        Core.flip(matInput, matInput, 1);
        Core.rotate(matInput,matInput,0);

        box = detect(cascadeClassifier_face, matInput.getNativeObjAddr(),
                matResult.getNativeObjAddr());
        warning = box.getDistance();

        if(box.getImage() ==0){
            nothingNum++;
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tvToast = new TextView(getApplicationContext());
                    String message = "가이드라인에 얼굴을 대주세요 \n계속 무반응시 종료됩니다!!";
                    tvToast.setText(message);
                    tvToast.setTextColor(Color.BLUE);
                    tvToast.setTextSize(20);
                    Toast toastView = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                    toastView.setView(tvToast);
                    toastView.setGravity(Gravity.CENTER, 0,0);
                    toastView.show();
                }
            }, 200);
            if(nothingNum >11) finish();
        }

        if (box.getImage()!=0){

            Mat test_img = new Mat(box.getImage());
            Size s = new Size(128,128);
            Imgproc.resize(test_img,test_img,s); //resize image same as training data
            Mat test = new Mat(1, test_img.rows()*test_img.cols(), CvType.CV_32FC1);


            int counter = 0;
            for (int rows = 0 ; rows<test_img.rows(); rows++){
                for(int cols = 0 ; cols<test_img.cols(); cols++){
                    test.put(0,counter,test_img.get(rows,cols));
                    counter++;
                }
            }
            double[] hog_image = hogImg(test_img);
            int hogArea = hog_image.length;
            Mat hogTest = new Mat(1,hogArea, CvType.CV_32FC1);
            for(int i = 0 ; i <hogArea; i++){
                double[] temp = new double[1];
                temp[0] = hog_image[i];
                hogTest.put(0,i,temp);
            }

//            SVM svm = SVM.load(fh.TEST_PATH+"train.xml");
//            float resultValue = svm.predict(test); //raw SVM
            float resultValue = 0;
            if (authFlag == false){
                resultValue = svm.predict(hogTest); //hog + SVM
            }
            if (resultValue == 1){
                authFlag = true;
                Log.d(TAG, "recognition: 인증 성공");


                //블루투스 어싱크태스크 객체 생성
                measureTask = new MeasureTask();
                measureTask.execute();

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext()
                                , "본인인증 확인!"
                                , Toast.LENGTH_SHORT).show();
                    }
                }, 0);

            }
            else{
                Log.d(TAG, "recognition: 인증 실패");
            }

        }

        if (warning >0){
            violenceNum++;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(violenceNum>4) finish();
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tvToast = new TextView(getApplicationContext());
                    String message = "가이드 라인을 \n벗어나지 마세요!!";
                    tvToast.setText(message);
                    tvToast.setTextColor(Color.RED);
                    tvToast.setTextSize(20);
                    Toast toastView = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                    toastView.setView(tvToast);
                    toastView.setGravity(Gravity.CENTER, 0,200);
                    toastView.show();
                }
            }, 0);
        }

//        Log.d(TAG, "matResult: "+matResult);
//        Size s2 = new Size(800,800);
//        Imgproc.resize(matResult,matResult,mat_copy.size());
        return matResult;
    }

    class MeasureTask extends AsyncTask{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"호기 측정 중입니다. 최소 4초 동안 길게 불어주세요",Snackbar.LENGTH_INDEFINITE);
            View snackbarView = snackbar.getView();
            TextView snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            snackbarView.setBackgroundColor(Color.GREEN);
            snackbarTextView.setTextColor(Color.BLACK);
            snackbarTextView.setTextSize(15);
            snackbar.show();


        }

        @Override
        protected Object doInBackground(Object[] objects) {
            while(isCancelled()==false && COvalues.size()<21){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                        public void onDataReceived(byte[] data, String message) {
                            Log.d(TAG, "COValue: "+ message.substring(1));
                            int temp = Integer.parseInt(message.substring(1));
                            COvalues.add(temp);
                            Log.d(TAG, "COValue size: "+ COvalues.size());
                        }
                    });
                }
            }

//            while(isCancelled()==false && COvalues.size()<21){ //테스트용
//                int temp = 1;
//                try {
//                    Thread.sleep(250);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                COvalues.add(temp);
//
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"측정이 끝났습니다.",Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            TextView snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            snackbarView.setBackgroundColor(Color.GREEN);
            snackbarTextView.setTextColor(Color.BLACK);
            snackbarTextView.setTextSize(15);
            snackbar.show();
            logUploadTask = new LogUploadTask();
            logUploadTask.execute();
        }
    }

    class LogUploadTask extends AsyncTask<Object,Integer,Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MeasureActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("측정값을 처리 중입니다...");
            dialog.show();

        }

        @Override
        protected Integer doInBackground(Object[] objects) {
            int decision = 0;

                int COsize = COvalues.size();
                int cnt_1 = 0;
                int cnt_2 = 0;
                for(int i = 0 ; i<COsize; i++) {
                    if (COvalues.get(i) == 0) continue;
                    else if (COvalues.get(i) == 1) {
                        cnt_1++;
                    } else { //2일때
                        cnt_2++;
                    }
                }
                if(cnt_1 + cnt_2 == 0){ // 불지 않은 경우 (0만 출력된 경우)
                    decision = 0;
                }
                else{ // 분 경우 (0을 제외한 값이 출력된 경우)
                    if(cnt_2 >= 2){ // 흡연한 경우 서버로 2 보냄
                        decision = 2;
                    }
                    else{ // 금연한 경우 서버로 1 보냄
                        decision = 1;
                    }
                }

                JsonObject measureResult = new JsonObject();
                measureResult.addProperty("user_id",user.getUserId());
                if(decision == 0 ||decision ==2){
                    measureResult.addProperty("result",0);
                }
                else{
                    measureResult.addProperty("result",1);
                }
                measureResult.addProperty("user_id",user.getUserId());
//                Call<JsonObject> res = userClient.measureLogging(measureResult);
                Call<JsonObject> res = ApiUtils.getUserClient().measureLogging(measureResult);
                res.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()){
                            dialog.dismiss();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t);

                    }
                });


            return decision;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(result==0){
                Toast.makeText(getApplicationContext(),"호기를 내뱉지 않았군요.. 자꾸 이러면 자격을 박탈당해요...",Toast.LENGTH_LONG).show();
            }
            else if(result ==1){
                Toast.makeText(getApplicationContext(),"금연 하셨군요!! 대단해요! 계속 노력해주세요!",Toast.LENGTH_SHORT).show();
            }
            else if(result==2){
                Toast.makeText(getApplicationContext(),"흡연 하셨군요...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
//    String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS  = {"android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

//                    if (!cameraPermissionAccepted)
//                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    }else
                    {
                        read_cascade_file();
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MeasureActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


}
