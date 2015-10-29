package com.seekdfa.seek.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.seekdfa.seek.R;
import com.seekdfa.seek.utilities.HttpTask;

/**
 * Created by jbruzek on 10/27/15.
 */
public class SearchResultsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.i("requestq", query);
            HttpTask task = new HttpTask();
            task.execute("https://maps.googleapis.com/maps/api/place/textsearch/xml?query=" + query + "&key=" + getResources().getString(R.string.server_key));
        }
    }
}
