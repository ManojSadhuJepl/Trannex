package trannex.ukkoteknik.com.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import trannex.ukkoteknik.com.R;


/**
 * Created by  Manoj Sadhu on 6/6/2018.
 */
public class ProgressDialogHelper {
    static ProgressDialogHelper progressDialogHelper;
    static AlertDialog progressDialog;

    private ProgressDialogHelper() {

    }

    public static ProgressDialogHelper getInstance(Context context) {
        if (progressDialogHelper == null) {
            progressDialogHelper = new ProgressDialogHelper();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false);
        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog = dialog;

        return progressDialogHelper;
    }

    public void showProgress() {
        progressDialog.show();
    }

    public void closeProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
