package com.example.android.moviesapp.com.example.android.moviesapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bo2o on 11/25/2016.
 */
public class MoviesDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";


    public MoviesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "create table " + MovieContract.MovieEntry.TABLE + " ( " +
                MovieContract.MovieEntry.MOVIE_ID +
                " INTEGER PRIMARY KEY," + MovieContract.MovieEntry.MOVIE_POSTER +  " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_TITLE +  " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_OVERVIEW +  " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_RATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_DATE + " TEXT NOT NULL " + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + MovieContract.MovieEntry.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}
