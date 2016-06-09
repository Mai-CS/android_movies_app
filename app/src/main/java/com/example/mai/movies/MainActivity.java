package com.example.mai.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mai.movies.data.MoviesDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if the device supports multi-pane or not
        MovieFragment.isMultiPane = (findViewById(R.id.detail_fragment_container) != null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //insert sort type into database
        MoviesDbHelper moviesDbHelper = new MoviesDbHelper(this);
        switch (item.getItemId()) {
            case R.id.action_favorites:
                moviesDbHelper.insertOption(1);
                break;
            case R.id.action_most_popular:
                moviesDbHelper.insertOption(2);
                break;
            case R.id.action_highest_rated:
                moviesDbHelper.insertOption(3);
                break;
        }
        //refresh the activity
        this.recreate();
        return super.onOptionsItemSelected(item);
    }


}