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
 * Created by jbruzek on 4/1/15.
 */
public class LocationsListFragment extends Fragment implements ParseCallbacks {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ParseHelper ph;
    private View v;
    private ArrayList<Location> locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.locations_list, container, false);
        container.removeAllViews();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = new ArrayList<Location>();
        mAdapter = new LoadingAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ph = new ParseHelper(this);
        ph.queryLocations();

        return v;
    }

    /**
     * unless there is something terribly wrong with out database, empty will always be true
     * @param empty
     */
    @Override
    public void complete(boolean empty) {
        locations = ph.getLocations();
        mAdapter = new LocationListAdapter(getActivity(), locations, empty, "Database Error", "No locations found");
        mRecyclerView.setAdapter(mAdapter);
    }

}

