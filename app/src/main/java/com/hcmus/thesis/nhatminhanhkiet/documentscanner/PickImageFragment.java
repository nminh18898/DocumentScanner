package com.hcmus.thesis.nhatminhanhkiet.documentscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.opencv.android.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PickImageFragment extends Fragment {
    private static final String TAG = "PickImageFragment";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMPORT_PHOTO = 2;


    String currentPhotoPath;
    Uri photoURI;

    private IScanner scanner;

    public static PickImageFragment newInstance(int actionCode) {

        Bundle args = new Bundle();
        args.putInt(ScanConstants.PICK_IMAGE_ACTION, actionCode);
        PickImageFragment fragment = new PickImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) context;
        int code = getArguments().getInt(ScanConstants.PICK_IMAGE_ACTION);

        if(code == ScanConstants.START_CAMERA_REQUEST_CODE){
            openCamera();
        }
        else if(code == ScanConstants.PICK_FILE_REQUEST_CODE){
            searchImage();
        }

    }

    public void searchImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMPORT_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_TAKE_PHOTO:
                    bitmap = createBitmap(photoURI);
                    break;
                case REQUEST_IMPORT_PHOTO:
                    if (data != null) {
                        Uri uri = data.getData();
                        bitmap = createBitmap(uri);
                    }
                break;
            }
        }
        else {
            getActivity().finish();
        }

        if (bitmap != null) {
            postImagePick(bitmap);
        }
        else {
            Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    Bitmap createBitmap(Uri uri){
        try {
            return getBitmap(uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmap(Uri selectedImage) throws IOException{
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor =
                    getActivity().getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }


    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.hcmus.thesis.nhatminhanhkiet.documentscanner.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addImageToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    protected void postImagePick(Bitmap bitmap) {
        Uri uri = com.hcmus.thesis.nhatminhanhkiet.documentscanner.Utils.getUri(getActivity(), bitmap);
        bitmap.recycle();
        scanner.onBitmapSelect(uri);
    }

}
