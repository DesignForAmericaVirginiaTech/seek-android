package com.designforamerica.seek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by jbruzek on 4/22/15.
 */






/**
 * The dialog for logging in the first time you use the app
 */
public class LoginDialog extends DialogFragment {

    public interface LoginDialogListener {
        public void onDialogPositiveClick(LoginDialog l);
        public void onDialogNegativeClick(LoginDialog l);
    }

    private LoginDialogListener mListener;
    private Button positive;
    private Button negative;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(v);

        positive = (Button) v.findViewById(R.id.login_with_facebook_button);
        negative = (Button) v.findViewById(R.id.continue_anonymously_button);

        final LoginDialog dis = this;
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogPositiveClick(dis);
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogNegativeClick(dis);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
