#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

//#include <string>
//
//extern "C" JNIEXPORT jstring JNICALL
//Java_com_example_stopsmoking_MeasureActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_stopsmoking_MeasureActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
//                                                              jlong matAddrInput,
//                                                              jlong matAddrResult) {
//
//    // TODO
//    // 입력 RGBA 이미지를 GRAY 이미지로 변환
//
//    Mat &matInput = *(Mat *)matAddrInput;
//
//    Mat &matResult = *(Mat *)matAddrResult;
//
//
//    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
//
//}

//class Box{
//    float distance;
//    Mat image;
//    public:
//        Box(float d, Mat img){
//            distance = d;
//            image = img;
//        }
//    };

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}

float euclideanDist(Point& p, Point& q){
    Point diff = p - q;
    return cv::sqrt(diff.x*diff.x + diff.y*diff.y);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_stopsmoking_MeasureActivity_loadCascade(JNIEnv *env, jobject instance,
                                                         jstring cascadeFileName_) {
//    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();
    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ", "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);

    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ", "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);
    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_stopsmoking_MeasureActivity_detect(JNIEnv *env, jobject instance,
                                                    jlong cascadeClassifier_face,
                                                    jlong matAddrInput, jlong matAddrResult) {

    jclass targetClass;
    jmethodID mid;
    jobject temp;
    jfieldID fid;
    targetClass = env->FindClass("com/example/stopsmoking/data/Box");
    mid = env->GetMethodID(targetClass,  "<init>", "()V");
    temp = env->NewObject(targetClass,mid); //create Object (initialize distance = 0)
    fid = env->GetFieldID(targetClass,"image","J"); //initialize mat addr = 0
    env->SetLongField(temp,fid,0);


    Mat &img_input = *(Mat *) matAddrInput;
//    Mat cetner_point =  &img_input / 2;
    Mat &img_result = *(Mat *) matAddrResult;

    cv::Size s = img_input.size();
    double rows = s.height;
    double cols = s.width;


    img_result = img_input.clone();
    Point center_point(cols  / 2, rows /2 );
    std::vector<Rect> faces;
    Mat img_gray;
    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);
    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);
    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ", (char *) "face %d found ", faces.size());

    ellipse(img_result, center_point, Size(350, 450), 0, 0, 360, Scalar(255,0,0),7,8);

    for (int i = 0; i < faces.size(); i++) {

        Point p1 = Point(faces[0].x, faces[0].y);
        Point p2 = Point(faces[0].x+faces[0].width, faces[0].y+faces[0].height);
        double face_radius = euclideanDist(p1,p2);
        if (face_radius >300){
            double real_facesize_x = faces[0].x / resizeRatio;
            double real_facesize_y = faces[0].y / resizeRatio;
            double real_facesize_width = faces[0].width / resizeRatio;
            double real_facesize_height = faces[0].height / resizeRatio;
            Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);

            float distance = euclideanDist(center, center_point);
            Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);
            Mat *faceROI = new Mat();
            *faceROI = img_gray( face_area );

            fid = env->GetFieldID(targetClass,"dist","F");
            env->SetFloatField(temp,fid, distance);

            if(distance >= 140){ // return calculated distance value
                fid = env->GetFieldID(targetClass,"image","J");
                env->SetLongField(temp,fid,0); //distance = value , mat addr = 0
                ellipse(img_result, center_point, Size(350, 450), 0, 0, 360, Scalar(255,0,0), 6, 8);
                return temp;

            }

            else{
                fid = env->GetFieldID(targetClass,"dist","F");
                env->SetFloatField(temp,fid, 0);
                fid = env->GetFieldID(targetClass,"image","J");
                env->SetLongField(temp,fid,jlong(faceROI));
                ellipse(img_result, center_point, Size(350, 450), 0, 0, 360, Scalar(0,255,0), 10, 8);
                return temp;
            }



//            std::vector<Rect> eyes;
            //— In each face, detect eyes
//        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(30, 30) );//
            //detecting eye
//        for ( size_t j = 0; j < eyes.size(); j++ )//
//        {//
//            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2, real_facesize_y + eyes[j].y + eyes[j].height/2 );//
//            int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );//
//            circle( img_result, eye_center, radius, Scalar( 255, 0, 0 ), 30, 8, 0 );//



        }

    }
    return temp;
}