package com.luxand.shg.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.luxand.shg.R;


public class ProgressBarDialog {
    private static Dialog progressDialog;

    public static void showLoadingDialog(Context context) {
        if (!(progressDialog != null && progressDialog.isShowing())) {
            progressDialog = new Dialog(context);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);

        }
    }

    public static void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}
