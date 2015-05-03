package com.designforamerica.seek;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * This class is an adapter for a recyclerview. It displays the Locations
 */
public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private ArrayList<Location> mDataset;
    private boolean empty = false;
    private String e1;
    private String e2;
    private Context context;
    private int type;

    /**
     * The viewholder class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView subtitle;
        ImageView image;
        Location l;

        public ViewHolder(View itemView) {
            super(itemView);

            if (!empty) {
                itemView.setClickable(true);
                itemView.setOnClickListener(this);
                title = (TextView) itemView.findViewById(R.id.location_item_title);
                image = (ImageView) itemView.findViewById(R.id.location_item_image);
            } else {
                title = (TextView) itemView.findViewById(R.id.no_locations_title);
                subtitle = (TextView) itemView.findViewById(R.id.no_locations_subtitle);
            }
        }

        /**
         * On click, open up a LocationActivity for the item clicked
         */
        @Override
        public void onClick(View v) {
            if (!empty) {
                //clicked an item in the list
                Intent i = new Intent(context, LocationActivity.class);
                i.putExtra("id", l.id());
                context.startActivity(i);
            }
        }
    }

    /**
     * Constructor for the LocationsAdapter
     *
     * @param error1 the title of the empty list error message
     * @param error2 the subtitle of the empty list error message
     * @param type 0: all locations 1: my locations 2: favorite locations
     */
    public LocationListAdapter(Context c, int type, String error1, String error2) {
        context = c;
        this.type = type;
        e1 = error1;
        e2 = error2;

        loadData();
    }

    /**
     * create the views to populate the recyclerview
     */
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view;
        if (empty) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_locations_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LocationListAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (!empty) {
            holder.l = mDataset.get(position);
            holder.title.setText(mDataset.get(position).name());
            if (holder.l.def()) {
                holder.image.setImageResource(R.drawable.ic_logo_primary);
            } else {
                holder.image.setImageResource(R.drawable.ic_logo_accent);
            }
        } else {
            //set an error message
            holder.title.setText(e1);
            holder.subtitle.setText(e2);
        }

    }

    /**
     * @return dataset size
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * load the location items into mDataSet based off what the type is
     */
    private void loadData() {
        switch(type) {
            case 0:
                mDataset = new ArrayList<Location>();
                mDataset.addAll(Seek.getLocations());
                empty = mDataset.isEmpty();
                break;
            case 1:
                mDataset = new ArrayList<Location>();
                mDataset.addAll(Seek.getMyLocations());
                empty = mDataset.isEmpty();

                break;
            case 2:
                mDataset = new ArrayList<Location>();
                mDataset.addAll(Seek.getFavoriteLocations());
                empty = mDataset.isEmpty();
                break;
        }

        //need to add a blank element if the query returned no results
        if (empty) {
            mDataset.add(new Location("", 0, 0, false, ""));
        }
    }
}