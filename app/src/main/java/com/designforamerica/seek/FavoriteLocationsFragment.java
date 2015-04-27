package com.designforamerica.seek;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Fragment that displays a list of the user's favorite locations.
 *
 * Created by jbruzek on 4/26/15.
 */
public class FavoriteLocationsFragment extends Fragment implements ParseCallbacks {

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

        //set up recyclerview
        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = new ArrayList<Location>();
        mAdapter = new LoadingAdapter();
        mRecyclerView.setAdapter(mAdapter);

        //query for favorite locations
        ph = new ParseHelper(this);
        ph.queryFavoriteLocations(ParseUser.getCurrentUser().getObjectId());

        return v;
    }

    /**
     * Background query has completed
     * @param empty
     */
    @Override
    public void complete(boolean empty) {
        locations = ph.getLocations();
        mAdapter = new LocationListAdapter(getActivity(), locations, empty, "You have no favorite Locations", "You can add a location to your favorites by clicking the star icon in the upper right of a location page.");
        mRecyclerView.setAdapter(mAdapter);
    }

}