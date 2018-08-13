package com.example.sergeygorun.opencvhello;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends RequestPermissionActivity implements CameraBridgeViewBase.CvCameraViewListener2, RequestPermissionActivity.OnRequestPermissionListener {
    private static final String TAG = "OPENCV-HELLO";
    private JavaCameraView javaCameraView;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };
    private Mat rgba;
    private Mat processedFrame;

    static {
        System.loadLibrary("MyLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isCameraPermissionGranted()) {
            requestCameraPermission(this);
        }

        try {
            OpenCVNative.loadCascade(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        javaCameraView = findViewById(R.id.opencv_camera);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCameraPermissionGranted()) {
            initOpenCV();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        rgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        rgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        rgba = inputFrame.rgba();
        processedFrame = new Mat();

        boolean isDetected = OpenCVNative.artDetection(rgba.getNativeObjAddr(),
                processedFrame.getNativeObjAddr());
        if (isDetected) {
//           rgba.release();
//           rgba = null;
           return processedFrame;
        }

        return rgba;
    }

    @Override
    public void onPermissionGranted() {
        initOpenCV();
    }

    private void initOpenCV() {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        }
    }

    @Override
    public void onPermissionDenied() {

    }
}
