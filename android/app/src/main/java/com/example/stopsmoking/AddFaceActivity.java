package com.example.stopsmoking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import com.example.stopsmoking.cvlibrary.Helpers.FileHelper;
import com.example.stopsmoking.cvlibrary.Helpers.MatName;
import com.example.stopsmoking.cvlibrary.Helpers.MatOperation;
import com.example.stopsmoking.data.UserData;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.core.CvType.CV_32SC1;


public class AddFaceActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String    TAG                 = "FaceDetection";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    private Mat mRgba;
    private Mat                    mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private String name;  // 추후 intent로 받아온 값으로 수정
    private String id;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.4f; //Detecting Size
    private int                    mAbsoluteFaceSize   = 0;
    private int total;
    private int numberOfPictures = 90;
    private CameraBridgeViewBase   mOpenCvCameraView;
    private UserData user;


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
    public AddFaceActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        Intent intentData = getIntent();
        user = (UserData) intentData.getParcelableExtra("OBJECT");

        name = user.getName();
        id = user.getUserId();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_add_face);
        total = 0;
        fh = new FileHelper();
        folder = "TEST";
        subfolder = "TEST_SUB";

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(800,600);
//        mOpenCvCameraView.setMaxFrameSize(288,352);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();

    }


    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        Mat rgb_copy = new Mat();
        mRgba.copyTo(rgb_copy);
        Mat gray_copy = new Mat();
        mRgba.copyTo(gray_copy);
        Core.flip(mGray, mGray, 1);
        Core.flip(mRgba, mRgba, 1);
//        Mat mRgbaT = mRgba.t();
//        Mat mGrayT = mGray.t();
//        Core.flip(mGrayT, mGray, 1);
//        Core.flip(mRgbaT, mRgba, 1);
        Core.rotate(mGray, mGray, 0);
        Core.rotate(mRgba, mRgba, 0);


        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();
        if (mJavaDetector != null)
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();
        if(facesArray.length >0){
            Mat img = new Mat(mGray, facesArray[0]);
            Size  s = new Size(128,128);
            Imgproc.resize(img,img,s);
            MatName m = new MatName(name + "_" + total, img);
                String wholeFolderPath = fh.TEST_PATH + id;
                new File(wholeFolderPath).mkdirs();
                fh.saveMatToImage(m, wholeFolderPath + "/");

//            if (folder.equals("Test")) {
//                String wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder;
//                new File(wholeFolderPath).mkdirs();
//                fh.saveMatToImage(m, wholeFolderPath + "/");
//            } else {
//                String wholeFolderPath = fh.TRAINING_PATH + name;
//                new File(wholeFolderPath).mkdirs();
//                fh.saveMatToImage(m, wholeFolderPath + "/");
//            }
            Imgproc.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 3);
            total++;
        }
//        Imgproc.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), FACE_RECT_COLOR, 3);
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }

        if(total >= numberOfPictures){
            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setResult(RESULT_OK, intent);
            finish();
        }



        Imgproc.resize(mGray, mGray, gray_copy.size());
        Imgproc.resize(mRgba, mRgba, rgb_copy.size());
        return mRgba;
    }

}
