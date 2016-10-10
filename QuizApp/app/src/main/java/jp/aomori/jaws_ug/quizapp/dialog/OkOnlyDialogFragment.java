package jp.aomori.jaws_ug.quizapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

public class OkOnlyDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener listener = null;

    public static OkOnlyDialogFragment newInstance(String title, String message, String buttonText) {
        OkOnlyDialogFragment instance = new OkOnlyDialogFragment();

        Bundle arguments = new Bundle();

        arguments.putString("title", title);
        arguments.putString("message", message);
        arguments.putString("buttonText", buttonText);

        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String buttonText = getArguments().getString("buttonText");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, which);
                        dismiss();
                    }
                });

        return alert.create();
    }

    public void setDialogListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }
}
