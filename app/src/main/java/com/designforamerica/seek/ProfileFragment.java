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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Fragment displaying information about the current user.
 * Picture, cover photo, name, email, and custom locations
 *
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

    /**
     * initialize
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        //get the profile information from the application
        url = Seek.getProfilePic();
        name = Seek.getName();
        email = Seek.getEmail();
        cover = Seek.getCoverPic();
        picture = (ImageView) v.findViewById(R.id.profile_picture);
        nameText = (TextView) v.findViewById(R.id.profile_name);
        emailText = (TextView) v.findViewById(R.id.profile_email);
        header = (ImageView) v.findViewById(R.id.profile_header);

        //set up the recyclerview
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_locations_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        locations = Seek.getMyLocations();
        mAdapter = new LocationListAdapter(getActivity(), locations, Seek.getMyLocationsEmpty(), "You have no locations", "Click the + button below to add a location to your profile");
        mRecyclerView.setAdapter(mAdapter);

        //load the profile picture and cover photo
        Picasso.with(v.getContext()).load(cover).placeholder(R.drawable.polygon).fit().centerCrop().transform(new BlurTransformation(getActivity(), 25)).into(header);
        Picasso.with(v.getContext()).load(url).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).transform(new CircleTransform()).into(picture);
        nameText.setText(name);
        emailText.setText(email);

        //Set up the floating action button
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

        return v;
    }

    /**
     * background query has completed.
     * @param empty
     */
    @Override
    public void complete(boolean empty) {
        locations = ph.getLocations();
        mAdapter = new LocationListAdapter(getActivity(), locations, empty, "You have no locations", "Click the + button below to add a location to your profile");
        mRecyclerView.setAdapter(mAdapter);
    }
}
