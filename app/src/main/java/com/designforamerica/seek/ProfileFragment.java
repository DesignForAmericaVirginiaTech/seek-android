package com.designforamerica.seek;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parse.ParseUser;
import com.software.shell.fab.ActionButton;
import com.squareup.picasso.Picasso;

/**
 * Created by jbruzek on 4/1/15.
 */
public class ProfileFragment extends Fragment {

    private ImageView picture;
    private TextView nameText;
    private TextView emailText;
    private ImageView header;
    private String url;
    private String name;
    private String email;
    private String cover;

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

        Picasso.with(v.getContext()).load(cover).fit().centerCrop().into(header);
        Picasso.with(v.getContext()).load(url).transform(new CircleTransform()).into(picture);
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

        return v;
    }
}
