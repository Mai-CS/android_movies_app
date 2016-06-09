package com.example.mai.movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mai.movies.data.MoviesContract;
import com.example.mai.movies.data.MoviesDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    static ArrayList<Movie> popularMoviesList = new ArrayList<>();
    static ArrayList<Movie> ratedMoviesList = new ArrayList<>();
    static ArrayList<Movie> favoritesList = new ArrayList<>();
    static Movie movie; //selected movie
    static int sortOption; //gets sort option from database
    static boolean isMultiPane; //determines device type either tablet or normal

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //get favorites from the database once the app launches
            getFavorites();

            if (isNetworkAvailable())
                //fetch movies once the app launches
                updateMovies();
            else
                Toast.makeText(getActivity(), "No network connection",
                        Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        movieAdapter = new MovieAdapter(getActivity());
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //add effect to ImageView when selected
                view.startAnimation(new AlphaAnimation(1F, 0.8F));
                if (sortOption != 1) {
                    if (isNetworkAvailable()) {
                        if (sortOption == 2)
                            movie = popularMoviesList.get(position);
                        else if (sortOption == 3)
                            movie = ratedMoviesList.get(position);

                        updateSelectedMovie();

                        if (favoritesList.contains(movie))
                            movie.setIsFavorite(true);
                    } else {
                        Toast.makeText(getActivity(), "No network connection",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    movie = favoritesList.get(position);
                    movie.setIsFavorite(true);
                }

                if (movie != null) {
                    if (!isMultiPane) {
                        //launch DetailActivity
                        startActivity(new Intent(getActivity(), DetailActivity.class));
                    } else {
                        //update right fragment
                        getFragmentManager().beginTransaction()
                                .replace(R.id.detail_fragment_container, new DetailFragment())
                                .commit();
                    }
                }

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            //get sort type from database
            sortOption = new MoviesDbHelper(getActivity()).getOption();
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                if (sortOption == 1)
                    actionBar.setTitle("Favorites");
                else if (sortOption == 2)
                    actionBar.setTitle("Most popular");
                else if (sortOption == 3)
                    actionBar.setTitle("Highest rated");
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Unexpected error!",
                    Toast.LENGTH_SHORT).show();
        }

        movieAdapter.notifyDataSetChanged();
    }

    public void updateMovies() {
        FetchMovieTask fetchMovieTask;
        /*
        attention: don't use same AsyncTask object to execute many times
         */
        try {
            //fetch most popular movies
            fetchMovieTask = new FetchMovieTask("1");
            popularMoviesList = new ArrayList<>(Arrays.asList(fetchMovieTask.execute(1).get()));

            //fetch highest rated movies
            fetchMovieTask = new FetchMovieTask("1");
            ratedMoviesList = new ArrayList<>(Arrays.asList(fetchMovieTask.execute(2).get()));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unexpected error!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSelectedMovie() {
        FetchMovieTask fetchMovieTask;
        Movie[] result;
        try {
            //fetch reviews
            fetchMovieTask = new FetchMovieTask("1");
            result = new Movie[1];
            System.arraycopy(fetchMovieTask.execute(3).get(), 0, result, 0, 1);
            movie.setAuthor(result[0].getAuthor());
            movie.setContent(result[0].getContent());

            //fetch trailers
            fetchMovieTask = new FetchMovieTask("1");
            movie.setKey((fetchMovieTask.execute(4).get())[0].getKey());
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unexpected error!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void getFavorites() {
        try {
            Cursor cursor = getActivity().getContentResolver()
                    .query(MoviesContract.FavoriteEntry.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Movie mv = new Movie();
                    mv.setId(cursor.getString(0));
                    mv.setTitle(cursor.getString(1));
                    mv.setPoster_path(cursor.getString(2));
                    mv.setBackdrop_path(cursor.getString(3));
                    mv.setRelease_date(cursor.getString(4));
                    mv.setVote_average(cursor.getString(5));
                    mv.setOverview(cursor.getString(6));
                    mv.setAuthor(cursor.getString(7));
                    mv.setContent(cursor.getString(8));
                    mv.setKey(cursor.getString(9));
                    favoritesList.add(mv);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unexpected error!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //check internet connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}