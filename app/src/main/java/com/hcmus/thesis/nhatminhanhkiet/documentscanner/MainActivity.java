package com.hcmus.thesis.nhatminhanhkiet.documentscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hcmus.thesis.nhatminhanhkiet.documentscanner.Adapter.ImageListAdapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_SCAN_ACTIVITY_CODE = 100;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private static final int REQUEST_PERMISSION_CODE = 200;

    private static boolean isPermissionGranted = false;

    FloatingActionButton fabTakeImage;

    RecyclerView rvImageList;

    ImageListAdapter adapter;
    ArrayList<String> imagePathList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupView();
        checkPermission();
        createImageList();

    }

    private void createImageList() {
        imagePathList =  getImagePath(retrieveImageFile());
        rvImageList.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageListAdapter(this, imagePathList);
        rvImageList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        imagePathList =  getImagePath(retrieveImageFile());
        adapter.notifyDataSetChanged();
    }

    void setupView(){
        fabTakeImage = findViewById(R.id.fabTakeImage);
        fabTakeImage.setOnClickListener(this);
        rvImageList = findViewById(R.id.rvImageList);
    }

    void checkPermission(){
        isPermissionGranted = hasPermission(PERMISSIONS);
        if(isPermissionGranted){

        }
        else {
            askPermission();
        }
    }

    public File[] retrieveImageFile(){
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/DocScanner/");
        folder.mkdirs();
        File[] allFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
            }
        });
        return allFiles;
    }

    public ArrayList<String> getImagePath(File[] files){
        ArrayList<String> filePathList = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            filePathList.add(files[i].getPath());
        }
        return filePathList;
    }

    boolean hasPermission(String... permissions){
        if(permissions != null){
            for(String permission:permissions){
                if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    void askPermission(){
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = true;
                    }
                    else
                    {
                        isPermissionGranted = false;
                    }
                }
            }
        }
    }


    protected void startScanActivity(int code){
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.PICK_IMAGE_ACTION, code);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_import:
                startScanActivity(ScanConstants.PICK_FILE_REQUEST_CODE);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN_ACTIVITY_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Toast.makeText(this, "Chosen"+ uri, Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabTakeImage:
                startScanActivity(ScanConstants.START_CAMERA_REQUEST_CODE);
                break;
        }
    }
}
