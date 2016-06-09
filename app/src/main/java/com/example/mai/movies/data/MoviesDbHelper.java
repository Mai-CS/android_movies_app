package com.example.mai.movies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mai.movies.Movie;
import com.example.mai.movies.data.MoviesContract.FavoriteEntry;
import com.example.mai.movies.data.MoviesContract.SortEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    /*should be increased when changing my database (ex: add or remove tables)
    to prevent old installations of the app from crashing
    */
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
//                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY, " +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_AUTHOR + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_CONTENT + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

        final String SQL_CREATE_OPTIONS_TABLE = "CREATE TABLE " + SortEntry.TABLE_NAME + " (" +
                SortEntry.COLUMN_SORT_OPTION + " INTEGER PRIMARY KEY " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_OPTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SortEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertFavorite(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdrop_path());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVote_average());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_AUTHOR, movie.getAuthor());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_CONTENT, movie.getContent());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_KEY, movie.getKey());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(FavoriteEntry.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }

    public void deleteFavorite(Movie movie) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(FavoriteEntry.TABLE_NAME,
                FavoriteEntry.COLUMN_MOVIE_ID + "=" + movie.getId(), null);
        sqLiteDatabase.close();
    }

//    public ArrayList<Movie> getFavorites() {
//        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
//        // * means every column
//        // 1 means every row
//        String DISPLAY_QUERY = "SELECT * FROM " + FavoriteEntry.TABLE_NAME + " WHERE 1";
//
//        // Cursor exact like ResultSet
//        Cursor cursor = sqLiteDatabase.rawQuery(DISPLAY_QUERY, null);
//        //cursor should start from the first row
//        cursor.moveToFirst();
//        ArrayList<Movie> favoritesData = new ArrayList<>();
//        while (!cursor.isAfterLast()) {
//            Movie movie = new Movie();
//            movie.setId(cursor.getString(0));
//            movie.setTitle(cursor.getString(1));
//            movie.setPoster_path(cursor.getString(2));
//            movie.setBackdrop_path(cursor.getString(3));
//            movie.setRelease_date(cursor.getString(4));
//            movie.setVote_average(cursor.getString(5));
//            movie.setOverview(cursor.getString(6));
//            movie.setAuthor(cursor.getString(7));
//            movie.setContent(cursor.getString(8));
//            movie.setKey(cursor.getString(9));
//            favoritesData.add(movie);
//            cursor.moveToNext();
//        }
//        sqLiteDatabase.close();
//        cursor.close();
//        return favoritesData;
//    }

    public void insertOption(int option) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SortEntry.COLUMN_SORT_OPTION, option);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //delete old value
        sqLiteDatabase.delete(SortEntry.TABLE_NAME, null, null);
        //insert new value
        sqLiteDatabase.insert(SortEntry.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

    }

    public int getOption() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String DISPLAY_QUERY = "SELECT * FROM " + SortEntry.TABLE_NAME + " WHERE 1";

        Cursor cursor = sqLiteDatabase.rawQuery(DISPLAY_QUERY, null);
        cursor.moveToFirst();
        int option;
        //check whether the table is empty or not
        if (cursor.getCount() > 0) {
            option = cursor.getInt(0);
        } else
            //if empty, set '2' default value
            option = 2;
        sqLiteDatabase.close();
        cursor.close();
        return option;
    }


}