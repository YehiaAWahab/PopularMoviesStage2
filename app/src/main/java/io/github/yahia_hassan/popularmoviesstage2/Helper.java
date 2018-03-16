package io.github.yahia_hassan.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Video;
import io.github.yahia_hassan.popularmoviesstage2.data.MovieContract;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class Helper {

    /**
     * Helper method that creates a new JSONObject from String.
     * @param jsonString
     * @return JSONObject or null if JSONException was thrown.
     */
    public static JSONObject createJSONObjectFromString (String TAG, String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException caught at createJSONObjectFromString: " + e);
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isMovieInFavoritesList(Context context, Movie movie) {
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_TITLE};
        String[] selectionArgs = {movie.getMovieTitle()};
        Uri uri = Uri.parse(MovieContract.BASE_CONTENT_URI + "/" + MovieContract.PATH_MOVIES);
        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + "=?",
                selectionArgs,
                null
        );
        if (cursor == null) {
            return false;
        } else if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public static void addAndDeleteMovieFromTheDatabase(Context context, Movie movie, FloatingActionButton floatingActionButton) {
        if (isMovieInFavoritesList(context, movie)) {
            Uri uri = Uri.parse(MovieContract.BASE_CONTENT_URI + "/" + MovieContract.PATH_MOVIES + "/" + movie.getMovieId());
            context.getContentResolver().delete(uri, null, null);
            floatingActionButton.setImageResource(R.drawable.ic_favorite_border);
        } else {
            ContentValues values = new ContentValues();

            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getMovieTitle());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getMoviePoster());
            values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getMoviePosterBackdrop());
            values.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());
            values.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getUserRating());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

            context.getContentResolver().insert(MovieContract.BASE_CONTENT_URI, values);

            floatingActionButton.setImageResource(R.drawable.ic_favorite);
        }
    }

}
