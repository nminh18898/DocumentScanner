package com.scanlibrary;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by jhansi on 28/03/15.
 */
public class ScanActivity {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }


    public static native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public static native Bitmap getGrayBitmap(Bitmap bitmap);

    public static native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public static native Bitmap getBWBitmap(Bitmap bitmap);

    public static native float[] getPoints(Bitmap bitmap);


}