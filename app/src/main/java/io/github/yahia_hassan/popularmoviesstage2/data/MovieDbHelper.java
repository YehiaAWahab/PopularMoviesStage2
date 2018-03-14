package io.github.yahia_hassan.popularmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PopularMovies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( "
            + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, "
            + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
