package com.designforamerica.seek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by jbruzek on 4/1/15.
 */
public class AllLocationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.locations_list, container, false);

        Bundle b = getArguments();

        TextView title = (TextView)v.findViewById(R.id.center_text);
        title.setText(b.getString("name"));

        return v;
    }
}
