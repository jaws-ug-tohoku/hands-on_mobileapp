package jp.aomori.jaws_ug.quizapp.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressDialogFragment extends DialogFragment {

    private static ProgressDialog progressDialog = null;

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment instance = new ProgressDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);

        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (progressDialog != null) {
            return progressDialog;
        }

        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);

        return progressDialog;
    }

    @Override
    public Dialog getDialog() {
        return progressDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }

}