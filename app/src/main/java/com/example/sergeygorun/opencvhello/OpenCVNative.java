package com.example.sergeygorun.opencvhello;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sergeygorun on 1/30/18.
 */

public class OpenCVNative {
    public native static boolean artDetection(long matAddrRgba, long processedAddr);

    public static void loadCascade(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.cascade);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "cascade_art.xml");
        FileOutputStream os = new FileOutputStream(mCascadeFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.close();

        Log.d("Haar hello", "cascade loaded: " + mCascadeFile.getAbsolutePath());
    }
}
