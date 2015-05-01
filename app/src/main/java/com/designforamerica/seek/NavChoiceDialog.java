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
import android.widget.LinearLayout;

/**
 * The dialog for logging in the first time you use the app
 * Created by jbruzek on 4/22/15.
 */
public class NavChoiceDialog extends DialogFragment {

    public interface NavChoiceDialogListener {
        public void onWalkClick(NavChoiceDialog l);
        public void onBicycleClick(NavChoiceDialog l);
        public void onDriveClick(NavChoiceDialog l);
    }

    private NavChoiceDialogListener mListener;
    private LinearLayout walk;
    private LinearLayout bike;
    private LinearLayout drive;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NavChoiceDialogListener) activity;
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
        View v = inflater.inflate(R.layout.dialog_nav_choice, null);
        builder.setView(v);

        walk = (LinearLayout) v.findViewById(R.id.walk_nav_choice);
        bike = (LinearLayout) v.findViewById(R.id.bike_nav_choice);
        drive = (LinearLayout) v.findViewById(R.id.drive_nav_choice);

        final NavChoiceDialog dis = this;
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWalkClick(dis);
                dismiss();
            }
        });
        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBicycleClick(dis);
                dismiss();
            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDriveClick(dis);
                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
