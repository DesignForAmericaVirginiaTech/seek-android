package com.designforamerica.seek;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jbruzek on 3/28/15.
 */
public class NavAdapter extends RecyclerView.Adapter<NavAdapter.ViewHolder>{
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private String profile;     //String URL for header view profile picture
    private String email;       //String Resource for header view email
    private String cover;
    private Context context;
    private NavDrawerCallbacks ndc;
    private int selected = 1;
    private ArrayList<ViewHolder> holders;


    /**
     * ViewHolders populate the RecyclerView. Each item in the list is contained in a VewHolder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int Holderid;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        ImageView cover;
        TextView Name;
        TextView email;
        Context context2;
        View parent;


        public ViewHolder(View itemView,int ViewType, Context c) {
            super(itemView);
            this.parent = itemView;

            context2 = c;

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.drawer_row_text);
                imageView = (ImageView) itemView.findViewById(R.id.drawer_row_icon);
                Holderid = 1; //row item

                holders.add(this);
            }
            else{
                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                cover = (ImageView) itemView.findViewById(R.id.nav_cover);
                Holderid = 0; //header item
            }
        }

        @Override
        public void onClick(View v) {
            ndc.itemSelected(getPosition());
            if (getPosition() == 0) {
                //you clicked the header
            }
            else {
                selected = getPosition();
                selectPosition(selected);
            }
        }


    }

    /**
     * The NavAdapter. Takes arguments from the Main Activity
     */
    NavAdapter(String Titles[],int Icons[],String Name, String Email, String Profile, String cover, Context passedContext, NavDrawerCallbacks n) {
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        this.cover = cover;
        profile = Profile;

        this.context = passedContext;
        this.ndc = n; //navDrawerCallbacks
        holders = new ArrayList<ViewHolder>();
    }

    @Override
    public NavAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_item,parent,false);
            ViewHolder vhItem = new ViewHolder(v,viewType, context);
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header,parent,false);
            ViewHolder vhHeader = new ViewHolder(v,viewType, context);
            return vhHeader;
        }

        return null;
    }

    /**
     * Bind the viewHolder to the Recyclerview list
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(NavAdapter.ViewHolder holder, int position) {
        if(holder.Holderid == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        }
        else{
            Log.d("profile url", profile);
            Picasso.with(context).load(cover).fit().centerCrop().into(holder.cover);
            Picasso.with(context).load(profile).transform(new CircleTransform()).into(holder.profile);
            //holder.profile.setImageResource(profile);
            holder.Name.setText(name);
            holder.email.setText(email);
        }

        //highlight the selected item
        if (selected == position) {
            holder.textView.setTextColor(context.getResources().getColor(R.color.primary));
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    /**
     * Header or Item?
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    /**
     * Mark a position as selected. Just for text highlighting.
     * @param position
     */
    private void selectPosition(int position) {
        ViewHolder vh;
        for (int i = 0; i < holders.size(); i++) {
            vh = holders.get(i);
            if ((i + 1) == selected) {
                vh.textView.setTextColor(context.getResources().getColor(R.color.primary));
            } else {
                vh.textView.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
    }

    /**
     * Header or nah?
     * @param position
     * @return
     */
    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
