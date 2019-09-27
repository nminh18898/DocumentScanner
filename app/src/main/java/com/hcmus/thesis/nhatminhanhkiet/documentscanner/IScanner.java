package com.hcmus.thesis.nhatminhanhkiet.documentscanner;

import android.net.Uri;

public interface IScanner {
    void onBitmapSelect(Uri uri);

    void onScanFinish(Uri uri);

    void onComplete();
}
