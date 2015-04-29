package com.designforamerica.seek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Fragment that lists all of the locations for the user to see.
 *
 * Created by jbruzek on 4/1/15.
 */
public class LocationsListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ParseHelper ph;
    private View v;
    private ArrayList<Location> locations;

    /**
     * initialize
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.locations_list, container, false);
        container.removeAllViews();

        //set up the recyclerview
        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = Seek.getLocations();
        //false because there will always be default locations
        mAdapter = new LocationListAdapter(getActivity(), locations, false, "Database Error", "No locations found");
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

}

