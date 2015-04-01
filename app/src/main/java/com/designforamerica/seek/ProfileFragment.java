package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseUser;

/**
 * Created by jbruzek on 4/1/15.
 */
public class ProfileFragment extends Fragment implements ParseCallbacks {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        /*
        Get the current user. This is commented for now because we haven't implemented login
        ParseUser user = ParseUser.getCurrentUser();
        See the documentation about how to get information from the user
        https://parse.com/docs/android_guide#users
        */

        Bundle b = getArguments();
        TextView text = (TextView)v.findViewById(R.id.sample_textView);
        text.setText(b.getString("name"));

        return v;
    }

    /**
     * Called when the ParseHelper finishes a task
     */
    @Override
    public void complete() {

    }
}
