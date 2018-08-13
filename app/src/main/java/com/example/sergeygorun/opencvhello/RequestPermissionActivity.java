package com.example.sergeygorun.opencvhello;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import junit.framework.Assert;

/**
 * Created by sergeygorun on 1/30/18.
 */

@SuppressLint("Registered")
public class RequestPermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

    interface OnRequestPermissionListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    OnRequestPermissionListener requestPermissionListener;

    protected void requestCameraPermission(@NonNull OnRequestPermissionListener requestPermissionListener) {
        this.requestPermissionListener = requestPermissionListener;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA },
                    PERMISSION_REQUEST_CODE_CAMERA);
        }
    }

    protected boolean isCameraPermissionGranted() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           final @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_CAMERA != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            //TODO handle fail
            return;
        }

        Thread initCamera = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            requestPermissionListener.onPermissionGranted();
                        } else {
                            requestPermissionListener.onPermissionDenied();
                        }

                    }
                });
            }
        });
        initCamera.start();
    }
}
