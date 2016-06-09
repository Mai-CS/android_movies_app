package com.example.mai.movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {

    private Context context;
    private MoviesDbHelper moviesDbHelper;

    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int FAVORITES = 101;

    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.FavoriteEntry.TABLE_NAME, FAVORITES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        context = getContext();
        moviesDbHelper = new MoviesDbHelper(context);
        return true;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return MoviesContract.FavoriteEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                SQLiteDatabase sqLiteDatabase = moviesDbHelper.getReadableDatabase();
                cursor = sqLiteDatabase.query(MoviesContract.FavoriteEntry.TABLE_NAME
                        , projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // call the code to actually do the query
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}