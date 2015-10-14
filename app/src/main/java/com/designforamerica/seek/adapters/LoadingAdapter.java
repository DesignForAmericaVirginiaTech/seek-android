package com.designforamerica.seek.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.designforamerica.seek.R;

import java.util.ArrayList;

/**
 * Populate a recyclerview with a loading animation
 */
public class LoadingAdapter extends RecyclerView.Adapter<LoadingAdapter.ViewHolder> {
    private ArrayList<Integer> mDataset;

    /**
     * The viewholder class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);

            progress = (ProgressBar) itemView.findViewById(R.id.loading_item_spinner);
        }
    }

    /**
     * Constructor for the LocationsAdapter
     */
    public LoadingAdapter() {
        mDataset = new ArrayList<Integer>();
        mDataset.add(0);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LoadingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //nothing
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
