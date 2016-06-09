package com.example.mai.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMovieTask extends AsyncTask<Integer, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private HttpURLConnection urlConnection;
    private BufferedReader reader;
    private String moviesJsonStr;
    private String page_num;

    public FetchMovieTask(String page_num) {
        this.page_num = page_num;
    }

    @Override
    protected Movie[] doInBackground(Integer... params) {
        Uri builtUri;
        String MOVIES_BASE_URL;
        final String API_KEY_PARAM = "api_key";
        if (params[0] == 1 || params[0] == 2) {
            //fetch movies by either most popularity ot highest rated
            MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String PAGE_PARAM = "page";
            final String SORT_BY_PARAM = "sort_by";
            String sortType;
            if (params[0] == 1) {
                sortType = "popularity.desc";
                builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortType)
                        .appendQueryParameter(PAGE_PARAM, page_num)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .build();
            } else {
                sortType = "vote_average.desc";
                final String VOTE_COUNT_PARAM = "vote_count.gte";
                final String RELEASE_DATE_PARAM = "release_date.gte";
                //build url
                builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortType)
                        .appendQueryParameter(VOTE_COUNT_PARAM, "1000")
                        .appendQueryParameter(RELEASE_DATE_PARAM, "2010-01-01")
                        .appendQueryParameter(PAGE_PARAM, page_num)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .build();
            }

            //build connection
            connect(builtUri);

            try {
                return getMovieDataFromJSON(moviesJsonStr, 1);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error: ", e);
            }

        } else if (params[0] == 3) {
            //fetch reviews
            MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            MOVIES_BASE_URL += MovieFragment.movie.getId() + "/reviews?";

            builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                    .build();

            connect(builtUri);

            try {
                return getMovieDataFromJSON(moviesJsonStr, 2);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error: ", e);
            }
        } else if (params[0] == 4) {
            //fetch trailers
            MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            MOVIES_BASE_URL += MovieFragment.movie.getId() + "/videos?";

            builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_API_KEY)
                    .build();

            connect(builtUri);

            try {
                return getMovieDataFromJSON(moviesJsonStr, 3);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error: ", e);
            }

        }

        return null;
    }

    //build connection
    private void connect(Uri builtUri) {
        try {
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            moviesJsonStr = builder.toString();
            Log.v(LOG_TAG, "Movie JSON String: " + moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: ", e);
                }
        }
    }

    private Movie[] getMovieDataFromJSON(String moviesJsonStr, int fetchType)
            throws JSONException {

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        final String OWM_RESULTS = "results";
        JSONArray moviesArray = moviesJSON.getJSONArray(OWM_RESULTS);

        int length = moviesArray.length();
        Movie[] results = new Movie[length];

        String keyStr = "";
        String authorStr = "";
        String contentStr = "";

        for (int i = 0; i < length; i++) {
            JSONObject movieDetails = moviesArray.getJSONObject(i);

            if (fetchType == 1) {
                final String OWM_ID = "id";
                final String OWM_TITLE = "title";
                final String OWM_POSTER_PATH = "poster_path";
                final String OWM_BACKDROP_PATH = "backdrop_path";
                final String OWM_RELEASE_DATE = "release_date";
                final String OWM_VOTE_AVERAGE = "vote_average";
                final String OWM_OVERVIEW = "overview";
                String id;
                String title;
                String poster_path;
                String backdrop_path;
                String release_date;
                String vote_average;
                String overview;

                id = movieDetails.getString(OWM_ID);
                title = movieDetails.getString(OWM_TITLE);
                poster_path = movieDetails.getString(OWM_POSTER_PATH);
                backdrop_path = movieDetails.getString(OWM_BACKDROP_PATH);
                release_date = movieDetails.getString(OWM_RELEASE_DATE);
                vote_average = movieDetails.getString(OWM_VOTE_AVERAGE);
                overview = movieDetails.getString(OWM_OVERVIEW);

                if (poster_path != null && !poster_path.equals("")) {
                    results[i] = new Movie();
                    results[i].setId(id);
                    results[i].setTitle(title);
                    results[i].setPoster_path(poster_path);
                    results[i].setBackdrop_path(backdrop_path);
                    results[i].setRelease_date(release_date);
                    results[i].setVote_average(vote_average);
                    results[i].setOverview(overview);
                }
            } else if (fetchType == 2) {
                final String OWM_AUTHOR = "author";
                final String OWM_CONTENT = "content";
                String author;
                String content;
                author = movieDetails.getString(OWM_AUTHOR);
                content = movieDetails.getString(OWM_CONTENT);
                if (i == 0) {
                    authorStr = author;
                    contentStr = content;
                } else {
                    authorStr += "#" + author;
                    contentStr += "#" + content;
                }
            } else if (fetchType == 3) {
                final String OWM_KEY = "key";
                String key;
                key = movieDetails.getString(OWM_KEY);
                if (i == 0)
                    keyStr = key;
                else
                    keyStr += "#" + key;
            }
        }

        if (fetchType == 2) {
            results = new Movie[1];
            results[0] = new Movie();
            results[0].setAuthor(authorStr);
            results[0].setContent(contentStr);
        } else if (fetchType == 3) {
            results = new Movie[1];
            results[0] = new Movie();
            results[0].setKey(keyStr);
        }

        return results;
    }


}