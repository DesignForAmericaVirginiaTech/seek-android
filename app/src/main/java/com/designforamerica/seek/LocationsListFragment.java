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
public class LocationsListFragment extends Fragment implements LocationListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View v;

    /**
     * initialize
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.locations_list, container, false);
        Seek.registerListener(this);

        //set up the recyclerview
        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        //false because there will always be default locations
        mAdapter = new LocationListAdapter(getActivity(), 0, "Database Error", "No locations found");
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    /**
     * callback from the Application class that the data has been changed
     */
    @Override
    public void locationsChanged() {
        mAdapter = new LocationListAdapter(getActivity(), 0, "Database Error", "No locations found");
        mRecyclerView.setAdapter(mAdapter);
    }

}

