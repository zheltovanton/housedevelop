package com.housedevelop.housedevelop;

import android.os.Bundle;
import android.content.Intent;
import android.app.SearchManager;
import android.widget.Toast;

public class SearchResultsActivity extends MapsActivity {

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
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
    }
}
