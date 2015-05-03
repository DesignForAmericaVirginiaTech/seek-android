package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jbruzek on 3/31/15.
 *
 * Code and examples for the sliding tabs taken from here:
 * http://stackoverflow.com/questions/20469877/adding-tab-inside-fragment-in-android
 */
public class LocationsFragment extends Fragment {

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.locations_fragment,container, false);
        container.removeAllViews();

        mTabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), ((android.support.v4.app.FragmentActivity)v.getContext()).getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b = new Bundle();
        b.putString("name", "All Locations");
        mTabHost.addTab(mTabHost.newTabSpec("ALL LOCATIONS").setIndicator("ALL LOCATIONS"),
                LocationsListFragment.class, b);
        b = new Bundle();
        b.putString("name", "Favorites");
        mTabHost.addTab(mTabHost.newTabSpec("FAVORITES").setIndicator("FAVORITES"),
                FavoriteLocationsFragment.class, b);

        //Add styles to the tabs
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_selector);
        mTabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab_selector);
        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.getTabWidget().setDividerDrawable(null);

        mTabHost.setCurrentTab(0);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTabHost.setCurrentTab(0);
    }
}
