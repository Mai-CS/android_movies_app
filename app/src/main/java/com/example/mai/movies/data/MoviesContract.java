package com.example.mai.movies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.mai.movies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";

        // "content://com.example.mai.movies/favorites"
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // column names
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_AUTHOR = "author";
        public static final String COLUMN_MOVIE_CONTENT = "content";
        public static final String COLUMN_MOVIE_KEY = "key";

        //return whole table
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

//        //return one row
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
//
//
//        public static Uri buildFavoriteUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }

    }

    public static final class SortEntry implements BaseColumns {

        public static final String TABLE_NAME = "options";

        public static final String COLUMN_SORT_OPTION = "sort_option";

    }


}