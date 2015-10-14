package com.designforamerica.seek.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.designforamerica.seek.adapters.LocationListAdapter;
import com.designforamerica.seek.interfaces.LocationListener;
import com.designforamerica.seek.R;
import com.designforamerica.seek.Seek;

/**
 * Fragment that displays a list of the user's favorite locations.
 *
 * Created by jbruzek on 4/26/15.
 */
public class FavoriteLocationsFragment extends Fragment implements LocationListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View v;

    /**
     * initialize
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.locations_list, container, false);
        container.removeAllViews();
        Seek.registerListener(this);

        //set up recyclerview
        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new LocationListAdapter(getActivity(), 2, "You have no favorite Locations", "You can add a location to your favorites by clicking the star icon in the upper right of a location page.");
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    /**
     * callback from the Application class that the data has been changed
     */
    @Override
    public void locationsChanged() {
        mAdapter = new LocationListAdapter(getActivity(), 2, "You have no favorite Locations", "You can add a location to your favorites by clicking the star icon in the upper right of a location page.");
        mRecyclerView.setAdapter(mAdapter);
    }

}
