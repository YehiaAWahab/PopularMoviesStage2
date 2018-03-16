package io.github.yahia_hassan.popularmoviesstage2.loaders;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.DetailsActivity;
import io.github.yahia_hassan.popularmoviesstage2.Helper;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.UriConstants;
import io.github.yahia_hassan.popularmoviesstage2.adapters.PopularMoviesAdapter;
import io.github.yahia_hassan.popularmoviesstage2.databinding.ActivityMainBinding;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivityAsyncTaskLoader implements LoaderManager.LoaderCallbacks<String>,
        PopularMoviesAdapter.MovieAdapterOnClickListener{

    private static final String TAG = MainActivityAsyncTaskLoader.class.getSimpleName();

    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String MOVIE_ID_STRING = "id";
    private static final String MOVIE_ORIGINAL_TITLE_STRING = "original_title";
    private static final String MOVIE_POSTER_PATH_STRING = "poster_path";
    private static final String MOVIE_Backdrop_POSTER_PATH_STRING = "backdrop_path";
    private static final String MOVIE_PLOT_SYNOPSIS_STRING = "overview";
    private static final String MOVIE_USER_RATING_STRING = "vote_average";
    private static final String MOVIE_RELEASE_DATE_STRING = "release_date";

    public static final int LOADER_ID = 45;

    private PopularMoviesAdapter mPopularMoviesAdapter;
    private Bundle mBundle;
    private Context mContext;
    private ActivityMainBinding mActivityMainBinding;


    public MainActivityAsyncTaskLoader (Context context, ActivityMainBinding activityMainBinding) {
        mContext = context;
        mActivityMainBinding = activityMainBinding;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        mBundle = args;
        return new AsyncTaskLoader<String>(mContext) {
            String mMovieJSON;

            @Nullable
            @Override
            public String loadInBackground() {
                String jsonString = null;
                OkHttpClient client = new OkHttpClient();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(UriConstants.SCHEME)
                        .authority(UriConstants.AUTHORITY)
                        .appendPath(UriConstants.VERSION_PATH)
                        .appendPath(UriConstants.MOVIE_PATH)
                        .appendPath(mBundle.getString(mContext.getString(R.string.main_activity_bundle_key)))
                        .appendQueryParameter(UriConstants.API_KEY_QUERY_PARAM, UriConstants.API_KEY);
                String url = builder.build().toString();
                Log.d(TAG, "The URL is: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    jsonString = response.body().string();
                } catch (IOException e) {
                    Log.e(TAG, "IOException caught at loadInBackground: " + e);
                    e.printStackTrace();
                }
                return jsonString;
            }

            @Override
            public void deliverResult(@Nullable String data) {
                mMovieJSON = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (mMovieJSON != null) {
                    deliverResult(mMovieJSON);
                } else {
                    showProgressBar();
                    forceLoad();
                }


            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        showRecyclerView();
        JSONObject rootJSONObject = Helper.createJSONObjectFromString(TAG, data);
        ArrayList<Movie> movieArrayList = null;

        if (rootJSONObject != null) {
            movieArrayList = createMovieArrayList(rootJSONObject);
        }

        mPopularMoviesAdapter = new PopularMoviesAdapter(mContext, movieArrayList, this);
        mActivityMainBinding.recyclerView.setAdapter(mPopularMoviesAdapter);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }


    /**
     * Creates a Movie ArrayList using a JSONObject.
     * @param rootJSONObject
     * @return
     */
    private static ArrayList<Movie> createMovieArrayList (JSONObject rootJSONObject) {
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        Movie movie;

        JSONArray resultsJSONArray = rootJSONObject.optJSONArray(RESULTS_JSON_ARRAY);
        int size = resultsJSONArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject nthJSONObject = resultsJSONArray.optJSONObject(i);
            String movieId = nthJSONObject.optString(MOVIE_ID_STRING);
            String movieTitle = nthJSONObject.optString(MOVIE_ORIGINAL_TITLE_STRING);
            String moviePoster = nthJSONObject.optString(MOVIE_POSTER_PATH_STRING);
            String movieBackdropPoster = nthJSONObject.optString(MOVIE_Backdrop_POSTER_PATH_STRING);
            String plotSynopsis = nthJSONObject.optString(MOVIE_PLOT_SYNOPSIS_STRING);
            String userRating = nthJSONObject.optString(MOVIE_USER_RATING_STRING);
            String releaseDate = nthJSONObject.optString(MOVIE_RELEASE_DATE_STRING);

            movie = new Movie(movieId,
                    movieTitle,
                    moviePoster,
                    movieBackdropPoster,
                    plotSynopsis,
                    userRating,
                    releaseDate);


            movieArrayList.add(movie);
        }
        return movieArrayList;
    }

    private void showProgressBar() {
        mActivityMainBinding.progressBar.setVisibility(View.VISIBLE);
        mActivityMainBinding.recyclerView.setVisibility(View.GONE);
        mActivityMainBinding.noNetworkTv.setVisibility(View.GONE);
        mActivityMainBinding.retryButton.setVisibility(View.GONE);

    }

    private void showRecyclerView() {
        mActivityMainBinding.recyclerView.setVisibility(View.VISIBLE);
        mActivityMainBinding.progressBar.setVisibility(View.GONE);
        mActivityMainBinding.noNetworkTv.setVisibility(View.GONE);
        mActivityMainBinding.retryButton.setVisibility(View.GONE);
    }

    @Override
    public void OnClick(Movie movie) {
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra(UriConstants.PARCELABLE_EXTRA_MESSAGE, movie);
        mContext.startActivity(intent);
    }
}
