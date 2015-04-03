package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.software.shell.fab.ActionButton;

/**
 * Created by jbruzek on 4/3/15.
 */
public class LocationFragment extends Fragment {

    private TextView title;
    private ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_fragment, container, false);
        container.removeAllViews();

        Bundle b = getArguments();

        title = (TextView) v.findViewById(R.id.location_title);
        title.setText(b.getString("title"));

        ActionButton actionButton = (ActionButton) v.findViewById(R.id.action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.accent));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.accentDark));
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_nav));
        actionButton.show();

        //Hide the toolbar when the user scrolls!
        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int dy = scrollView.getScrollY();
                if (dy >= 300) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
                } else if (dy < 300) {
                    ((ActionBarActivity)getActivity()).getSupportActionBar().show();
                }
            }
        });

        return v;
    }


}
