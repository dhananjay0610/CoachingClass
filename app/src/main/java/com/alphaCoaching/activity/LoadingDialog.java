package com.alphaCoaching.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.alphaCoaching.R;

class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activitys) {
        activity=activitys;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.customdialog,null));
        builder.setCancelable(false);
        dialog=builder.create();
        dialog.show();

    }

    void dismiss(){
        dialog.dismiss();
    }
}
