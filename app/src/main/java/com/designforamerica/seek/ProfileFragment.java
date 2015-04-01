package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jbruzek on 4/1/15.
 */
public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        Bundle b = getArguments();
        TextView text = (TextView)v.findViewById(R.id.sample_textView);
        text.setText(b.getString("name"));

        return v;
    }

}
