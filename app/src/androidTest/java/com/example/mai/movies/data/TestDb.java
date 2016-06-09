package com.example.mai.movies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    // Since we want each test to start with a clean state
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MoviesContract.FavoriteEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the favorite entry
        assertTrue("Error: Your database was created without favorites entry table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.FavoriteEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favoriteColumnHashSet = new HashSet<>();
//        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry._ID);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_TITLE);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_BACKDROP_PATH);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_AUTHOR);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_CONTENT);
        favoriteColumnHashSet.add(MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favoriteColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required favorite
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favorite entry columns",
                favoriteColumnHashSet.isEmpty());
        db.close();
        c.close();
    }


}