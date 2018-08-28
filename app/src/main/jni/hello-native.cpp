#include <hello-native.h>
CascadeClassifier artClassifier;
RNG rng(12345);

JNIEXPORT jboolean JNICALL Java_com_example_sergeygorun_opencvhello_OpenCVNative_artDetection
  (JNIEnv *env, jclass object, jlong matRgba, jlong outputFrameAddr) {
    Mat& frame = *(Mat*)matRgba;
    Mat& outputFrame = *(Mat*)outputFrameAddr;

    if (artClassifier.empty()) {
        // TODO opencv cannot load cascade from android resources
        String cascade_name = "/data/user/0/com.example.sergeygorun.opencvhello/app_cascade/cascade_art.xml";
        if( !artClassifier.load( cascade_name ) ){
            // :(((
            __android_log_print(ANDROID_LOG_DEBUG, "Haar Hello", "Fail to load cascade");

        }
    } else {
        Rect* patternRoi = artHaarDetection(frame);
        if (patternRoi != NULL) {
           detectContours(frame, *patternRoi);
        }
    }

    return false;
}

Rect* artHaarDetection(Mat& frame) {
    std::vector<Rect> patterns;
    Mat frame_gray;
    cvtColor( frame, frame_gray, COLOR_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    artClassifier.detectMultiScale( frame_gray, patterns, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );
    for ( size_t i = 0; i < patterns.size(); i++ )
    {
        __android_log_print(ANDROID_LOG_DEBUG, "Haar Hello", "cascade detected: %d", (int)(i + 1));
        Point center( patterns[i].x + patterns[i].width/2, patterns[i].y + patterns[i].height/2 );
        ellipse( frame, center, Size( patterns[i].width/2, patterns[i].height/2 ), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
        return &(patterns[0]);
    }

    return NULL;
}

void detectContours(Mat& frame, Rect patternRoi) {
    Mat srcGray;
    Mat dst;
    cvtColor(frame, srcGray, COLOR_BGR2GRAY);

    //apply adaptive threshold, inverse colors
    adaptiveThreshold(srcGray(patternRoi), dst, 255.0,
        ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 51, 61);

    //apply opening op
    int morph_size = 2;
    Mat element = getStructuringElement( MORPH_RECT, Size( 2*morph_size + 1, 2*morph_size+1 ),
        Point( morph_size, morph_size ) );
    morphologyEx( dst, dst, MORPH_CLOSE, element );

    //find contours
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    findContours( dst, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

    // filter contours
    vector<vector<Point> > resContours;
    for( int i = 0; i < contours.size(); i++ ) {
         if (arcLength(contours[i], true) > 20) {
            resContours.push_back(contours[i]);
         }
    }

    __android_log_print(ANDROID_LOG_DEBUG, "Haar Hello", "detected countours: %d", (int)resContours.size());
    /// Draw contours
    if (resContours.size() < 10) {
    for( int i = 0; i < resContours.size(); i++ )
        {
            Scalar color = Scalar( 200, 80, 80 );
            drawContours( frame, resContours, i, color, 2, 8, hierarchy, 0, Point(patternRoi.x, patternRoi.y) );
        }
    }

}