package com.hcmus.thesis.nhatminhanhkiet.documentscanner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static String TAG = "Utils";

    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public static void deleteBitmap(Context context, Uri uri){
        File file = new File(uri.getPath());
        Toast.makeText(context, String.valueOf(file.isFile()), Toast.LENGTH_SHORT).show();

        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, "File Deleted", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"File deleted: " + uri.getPath());
            } else {
                Toast.makeText(context, "File Not Deleted", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"File not deleted: " + uri.getPath());
            }
        }
        else {
            Toast.makeText(context, "File not exist" + uri, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean saveImageToExternalStorage(Context context, Bitmap image) {
        try
        {
            OutputStream fOut = null;

            File file = createImageFile();

            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return true;

        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }

    }


    public static File createFile(String fileType, String path) throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(System.currentTimeMillis());
        File storageDir = new File(path);
        if (!storageDir.exists())
            storageDir.mkdirs();
        File file = File.createTempFile(
                timeStamp,
                "." + fileType,
                storageDir
        );
        return file;
    }



    public static File createImageFile() throws IOException {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/DocScanner/";
        return createFile("jpeg", path);
    }

    public static File createPDFFile() throws IOException {
        String path = Environment.getExternalStorageDirectory() + "/DocScanner/";
        return createFile("pdf", path);
    }


}
