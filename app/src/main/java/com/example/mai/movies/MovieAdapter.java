package com.example.mai.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class MovieAdapter extends BaseAdapter {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context context;
    private static int most_popular_page_num = 1;
    private static int highest_rated_page_num = 1;

    public MovieAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        if (MovieFragment.sortOption == 1)
            return MovieFragment.favoritesList.size();
        else if (MovieFragment.sortOption == 2)
            return MovieFragment.popularMoviesList.size();
        else if (MovieFragment.sortOption == 3)
            return MovieFragment.ratedMoviesList.size();
        else
            return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        ImageView imageView;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item_movie, parent, false);
        } else {
            view = convertView;
        }
        //make list scrolling smoother
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view_item);
        view.setTag(viewHolder);
        try {
            String posterUrl = "http://image.tmdb.org/t/p/w185/";
            if (MovieFragment.sortOption == 1) {
                posterUrl += MovieFragment.favoritesList.get(position).getPoster_path();
            } else if (MovieFragment.sortOption == 2) {
                posterUrl += MovieFragment.popularMoviesList.get(position).getPoster_path();
            } else if (MovieFragment.sortOption == 3) {
                posterUrl += MovieFragment.ratedMoviesList.get(position).getPoster_path();
            }
            //load image
            Picasso.with(context).load(posterUrl).into(viewHolder.imageView);

            //check if user reached to the end of gridView
            //if yes, fetch more movies
            if (MovieFragment.sortOption == 2) {
                if (position == MovieFragment.popularMoviesList.size() - 1) {
                    most_popular_page_num++;
                    loadMoreMovies();
                }
            } else if (MovieFragment.sortOption == 3)
                if (position == MovieFragment.ratedMoviesList.size() - 1) {
                    highest_rated_page_num++;
                    loadMoreMovies();
                }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Download Error:", e);
        }
        return view;
    }

    //fetch more movies
    public void loadMoreMovies() {
        FetchMovieTask fetchMovieTask;
        try {
            if (isNetworkAvailable()) {
                if (MovieFragment.sortOption == 2) {
                    fetchMovieTask = new FetchMovieTask(String.valueOf(most_popular_page_num));
                    MovieFragment.popularMoviesList.addAll(
                            new ArrayList<>(Arrays.asList(fetchMovieTask.execute(1).get())));
                } else if (MovieFragment.sortOption == 3) {
                    fetchMovieTask = new FetchMovieTask(String.valueOf(highest_rated_page_num));
                    MovieFragment.ratedMoviesList.addAll(
                            new ArrayList<>(Arrays.asList(fetchMovieTask.execute(2).get())));
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unexpected error!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    //check internet connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}