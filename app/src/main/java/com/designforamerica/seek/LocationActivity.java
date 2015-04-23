package com.designforamerica.seek;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.software.shell.fab.ActionButton;

/**
 * Created by jbruzek on 4/2/15.
 */
public class LocationActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private RelativeLayout layout;
    private LocationFragment locFrag;

    private boolean favorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.location_tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.inflateMenu(R.menu.location);

        if (getFragmentManager().findFragmentById(R.layout.location_fragment) == null) {
            locFrag = new LocationFragment();
        }
        Bundle b = new Bundle();
        b.putString("title", intent.getStringExtra("title"));
        b.putDouble("lat", intent.getDoubleExtra("lat", 0));
        b.putDouble("lon", intent.getDoubleExtra("lon", 0));
        locFrag.setArguments(b);
        getFragmentManager().beginTransaction().add(R.id.location_container, locFrag).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        if (id == R.id.favorite) {
            if (favorite) {
                Drawable d = item.getIcon();
                d.clearColorFilter();
                item.setIcon(d);
                favorite = false;
            } else {
                Drawable d = item.getIcon();
                d.setColorFilter( 0xffffff00, PorterDuff.Mode.MULTIPLY );
                item.setIcon(d);
                favorite = true;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
