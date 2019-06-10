package com.example.stopsmoking;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.stopsmoking.cvlibrary.Helpers.FileHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Measure2Activity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG  = "FaceVerification";
    private Mat mRgba;
    private Mat mGray;
    public static final int JAVA_DETECTOR = 0;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private String name="jsw";  // 추후 intent로 받아온 값으로 수정
    private FileHelper fh;
    private String[] mDetectorName;
    private float mRelativeFaceSize  = 0.4f; //Detecting Size
    private int  mAbsoluteFaceSize  = 0;
    private CameraBridgeViewBase   mOpenCvCameraView;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("detection_based_tracker");
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Measure2Activity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_add_face);
        fh = new FileHelper();

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(800,600);
//        mOpenCvCameraView.setMaxFrameSize(288,352);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
}
