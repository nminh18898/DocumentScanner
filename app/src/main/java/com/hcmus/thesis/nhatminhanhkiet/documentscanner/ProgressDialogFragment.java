package com.hcmus.thesis.nhatminhanhkiet.documentscanner;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.DialogFragment;


public class ProgressDialogFragment extends DialogFragment {
    public String message;


    public static ProgressDialogFragment newInstance(String msg) {
        Bundle args = new Bundle();
        args.putString("message",msg);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        message = getArguments().getString("message");
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        // Disable the back button
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }

        };
        dialog.setOnKeyListener(keyListener);
        return dialog;
    }
}
