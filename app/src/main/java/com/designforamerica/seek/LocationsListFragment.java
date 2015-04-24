package com.designforamerica.seek;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jbruzek on 4/1/15.
 */
public class LocationsListFragment extends Fragment implements ParseCallbacks {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ParseHelper ph;
    private View v;
    private ArrayList<Location> locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.locations_list, container, false);
        container.removeAllViews();

        Bundle b = getArguments();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = new ArrayList<Location>();
        mAdapter = new LocationsAdapter(locations);
        mRecyclerView.setAdapter(mAdapter);

        ph = new ParseHelper(this);
        ph.queryLocations();

        return v;
    }


    @Override
    public void complete() {
        locations = ph.getLocations();
        mAdapter = new LocationsAdapter(locations);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This class is an adapter for the recyclerview. It displays the Locations
     */
    public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
        private ArrayList<Location> mDataset;

        /**
         * The viewholder class.
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            TextView title;
            ImageView image;
            Location l;

            public ViewHolder(View itemView) {
                super(itemView);

                itemView.setClickable(true);
                itemView.setOnClickListener(this);

                title = (TextView) itemView.findViewById(R.id.location_item_title);
                image = (ImageView) itemView.findViewById(R.id.location_item_image);
            }

            @Override
            public void onClick(View v) {
                Log.d("onClickLocation", l.longitude() + " " + l.latitude());
                //clicked an item in the list
                Intent i = new Intent(getActivity(), LocationActivity.class);
                i.putExtra("title",  l.name());
                i.putExtra("lon", (Double)l.longitude());
                i.putExtra("lat", (Double)l.latitude());
                startActivity(i);
            }
        }

        /**
         * Constructor for the LocationsAdapter
         * @param myDataset
         */
        public LocationsAdapter(ArrayList<Location> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.title.setText(mDataset.get(position).name());
            holder.image.setImageResource(R.drawable.ic_logo_accent);
            holder.l = mDataset.get(position);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


}

