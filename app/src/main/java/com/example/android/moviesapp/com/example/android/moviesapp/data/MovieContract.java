package com.example.android.moviesapp.com.example.android.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Bo2o on 12/2/2016.
 */
public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.moviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE = "movies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_POSTER = "poster";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_RATE = "rate";
        public static final String MOVIE_DATE = "date";
        public static final String MOVIE_OVERVIEW = "overview";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE;

        public static Uri buildMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}