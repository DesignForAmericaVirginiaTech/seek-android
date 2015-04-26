package com.designforamerica.seek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jbruzek on 4/1/15.
 */
public class ProfileFragment extends Fragment implements ParseCallbacks {

    private ParseHelper ph;
    private ImageView picture;
    private TextView nameText;
    private TextView emailText;
    private ImageView header;
    private String url;
    private String name;
    private String email;
    private String cover;
    private ArrayList<Location> locations;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        /*
        Get the current user. This is commented for now because we haven't implemented login
        ParseUser user = ParseUser.getCurrentUser();
        See the documentation about how to get information from the user
        https://parse.com/docs/android_guide#users
        */

        Bundle b = getArguments();
        url = b.getString("url");
        name = b.getString("name");
        email = b.getString("email");
        cover = b.getString("cover");
        picture = (ImageView) v.findViewById(R.id.profile_picture);
        nameText = (TextView) v.findViewById(R.id.profile_name);
        emailText = (TextView) v.findViewById(R.id.profile_email);
        header = (ImageView) v.findViewById(R.id.profile_header);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = new ArrayList<Location>();
        mAdapter = new MyLocationsAdapter(locations);
        mRecyclerView.setAdapter(mAdapter);

        Picasso.with(v.getContext()).load(cover).placeholder(R.drawable.polygon).fit().centerCrop().transform(new BlurTransformation(getActivity(), 25)).into(header);
        Picasso.with(v.getContext()).load(url).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).transform(new CircleTransform()).into(picture);
        nameText.setText(name);
        emailText.setText(email);

        ActionButton actionButton = (ActionButton) v.findViewById(R.id.add_location_button);
        actionButton.setButtonColor(getResources().getColor(R.color.accent));
        actionButton.setButtonColorPressed(getResources().getColor(R.color.accentDark));
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));
        actionButton.show();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is stuff
            }
        });

        ph = new ParseHelper(this);
        ph.queryMyLocations(ParseUser.getCurrentUser().getObjectId());

        return v;
    }

    @Override
    public void complete() {
        locations = ph.getLocations();
        mAdapter = new MyLocationsAdapter(locations);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This class is an adapter for the recyclerview. It displays the Locations
     */
    public class MyLocationsAdapter extends RecyclerView.Adapter<MyLocationsAdapter.ViewHolder> {
        private ArrayList<Location> mDataset;
        private boolean empty = false;

        /**
         * The viewholder class.
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            TextView title;

            public ViewHolder(View itemView) {
                super(itemView);

                itemView.setClickable(true);
                itemView.setOnClickListener(this);

                if (!empty) {
                    title = (TextView) itemView.findViewById(R.id.my_location_title);
                }
            }

            @Override
            public void onClick(View v) {
                //clicked an item in the list
            }
        }

        /**
         * Constructor for the LocationsAdapter
         * @param myDataset
         */
        public MyLocationsAdapter(ArrayList<Location> myDataset) {
            mDataset = myDataset;
            //need to add a blank element if the list is empty
            if (mDataset.isEmpty()) {
                empty = true;
                mDataset.add(new Location("", 0, 0));
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyLocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // create a new view
            View view;
            if (empty) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_locations_list_item, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_locations_list_item, parent, false);
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
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
