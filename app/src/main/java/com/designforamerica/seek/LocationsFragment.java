package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by jbruzek on 3/31/15.
 *
 * Code and examples for the sliding tabs taken from here:
 * https://github.com/astuetz/PagerSlidingTabStrip
 */
public class LocationsFragment extends Fragment {

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.locations_fragment,container, false);

        mTabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), ((android.support.v4.app.FragmentActivity)v.getContext()).getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("ALL LOCATIONS").setIndicator("ALL LOCATIONS"),
                AllLocationsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("FAVORITES").setIndicator("FAVORITES"),
                FavoriteLocationsFragment.class, null);

        //Add styles to the tabs
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_selector);
        mTabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab_selector);
        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.getTabWidget().setDividerDrawable(null);

        return v;
    }
}
