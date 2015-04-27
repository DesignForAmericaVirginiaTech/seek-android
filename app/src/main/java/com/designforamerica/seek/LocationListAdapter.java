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
    private ProgressBar progress;
    private String e1;
    private String e2;
    private Context context;

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

        @Override
        public void onClick(View v) {
            if (!empty) {
                Log.d("onClickLocation", l.longitude() + " " + l.latitude());
                //clicked an item in the list
                Intent i = new Intent(context, LocationActivity.class);
                i.putExtra("title", l.name());
                i.putExtra("lon", (Double) l.longitude());
                i.putExtra("lat", (Double) l.latitude());
                context.startActivity(i);
            }
        }
    }

    /**
     * Constructor for the LocationsAdapter
     *
     * @param myDataset
     * @param empty is the result an empty list>
     * @param error1 the title of the empty list error message
     * @param error2 the subtitle of the empty list error message
     */
    public LocationListAdapter(Context c, ArrayList<Location> myDataset, boolean empty, String error1, String error2) {
        context = c;
        mDataset = myDataset;
        this.empty = empty;
        e1 = error1;
        e2 = error2;
        //need to add a blank element if the query returned no results
        if (empty) {
            mDataset.add(new Location("", 0, 0));
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("Profile", "added a location to the recyclerview");
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (!empty) {
            holder.title.setText(mDataset.get(position).name());
            holder.image.setImageResource(R.drawable.ic_logo_accent);
            holder.l = mDataset.get(position);
        } else {
            holder.title.setText(e1);
            holder.subtitle.setText(e2);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}