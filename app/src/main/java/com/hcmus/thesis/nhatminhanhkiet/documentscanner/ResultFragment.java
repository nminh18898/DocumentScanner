package com.hcmus.thesis.nhatminhanhkiet.documentscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class ResultFragment extends Fragment implements View.OnClickListener{

    private View view;
    private ImageView ivScanImage;
    private Bitmap originalBitmap;
    private IScanner scanner;
    private Button btnSave, btnCancel;

    private static ProgressDialogFragment progressDialogFragment;
    private static ProgressBar pbLoading;

    public ResultFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_fragment_layout, null);
        init();
        return view;
    }

    private void init(){
        ivScanImage = view.findViewById(R.id.ivScanImage);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        pbLoading = view.findViewById(R.id.pbLoading);

        Bitmap bitmap = getBitmap();
        setScannedImage(bitmap);
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            originalBitmap = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return originalBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        Uri uri = getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
        return uri;
    }

    public void setScannedImage(Bitmap scannedImage) {
        /*Glide.with(getActivity()).load(scannedImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(ivScanImage);*/
        ivScanImage.setImageBitmap(scannedImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                Utils.saveImageToExternalStorage(getActivity(), originalBitmap);
                break;
            case R.id.btnCancel:
                createPdf(originalBitmap);
                break;
        }
        scanner.onComplete();
    }

    private void createPdf(Bitmap bitmap){
        pbLoading.setVisibility(View.VISIBLE);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);



        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);


        // write the document content
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(System.currentTimeMillis());
        String targetPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/DocScanner/" + timeStamp + ".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong)+ e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        pbLoading.setVisibility(View.GONE);
    }




    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = ProgressDialogFragment.newInstance(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(scanner != null){
            scanner = null;
        }
    }
}
