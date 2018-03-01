package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        PopularMoviesAdapter.MovieAdapterOnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MESSAGE = "DetailActivity key";

    private static final String API_KEY = BuildConfig.API_KEY;

    public static final int LOADER_ID = 45;


    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private GridLayoutManager mLayoutManager;
    private CoordinatorLayout mCoordinatorLayout;
    private ProgressBar mProgressBar;


    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String MOVIE_ORIGINAL_TITLE_STRING = "original_title";
    private static final String MOVIE_POSTER_PATH_STRING = "poster_path";
    private static final String MOVIE_PLOT_SYNOPSIS_STRING = "overview";
    private static final String MOVIE_USER_RATING_STRING = "vote_average";
    private static final String MOVIE_RELEASE_DATE_STRING = "release_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mProgressBar = findViewById(R.id.progress_bar);

        /*
         * I search online how to get the Activity orientation and find the solution here
          * on Stack Overflow ( https://stackoverflow.com/a/11381854/5255289 )
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 3);
        } else {
            mLayoutManager = new GridLayoutManager(this, 2);
        }


        mRecyclerView.setLayoutManager(mLayoutManager);


        if (isNetworkAvailable()) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            showNoNetworkSnackbar();
        }

    }

    @Override
    public void OnClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, movie);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            //String mMovieJSON;

            @Nullable
            @Override
            public String loadInBackground() {
                String jsonString = null;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String popularOrTopRated = sharedPreferences.getString(
                        getString(R.string.pref_sort_by_key),
                        getString(R.string.pref_sort_popular));

                OkHttpClient client = new OkHttpClient();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(UriConstants.SCHEME)
                        .authority(UriConstants.AUTHORITY)
                        .appendPath(UriConstants.VERSION_PATH)
                        .appendPath(UriConstants.MOVIE_PATH)
                        .appendPath(popularOrTopRated)
                        .appendQueryParameter(UriConstants.API_KEY_QUERY_PARAM, API_KEY);
                String url = builder.build().toString();
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
                CacheJSON.mMovieJSON = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (CacheJSON.mMovieJSON != null) {
                    deliverResult(CacheJSON.mMovieJSON);
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
        JSONObject rootJSONObject = createJSONObjectFromString(data);
        ArrayList<Movie> movieArrayList = null;

        if (rootJSONObject != null) {
            movieArrayList = createMovieArrayList(rootJSONObject);
        }

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, movieArrayList, this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
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
            String movieTitle = nthJSONObject.optString(MOVIE_ORIGINAL_TITLE_STRING);
            String moviePoster = nthJSONObject.optString(MOVIE_POSTER_PATH_STRING);
            String plotSynopsis = nthJSONObject.optString(MOVIE_PLOT_SYNOPSIS_STRING);
            String userRating = nthJSONObject.optString(MOVIE_USER_RATING_STRING);
            String releaseDate = nthJSONObject.optString(MOVIE_RELEASE_DATE_STRING);

            movie = new Movie(movieTitle,
                    moviePoster,
                    plotSynopsis,
                    userRating,
                    releaseDate);

            movieArrayList.add(movie);
        }
        return movieArrayList;
    }

    /**
     * Helper method that creates a new JSONObject from String.
     * @param jsonString
     * @return JSONObject or null if JSONException was thrown.
     */
    private static JSONObject createJSONObjectFromString (String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException caught at createJSONObjectFromString: " + e);
            e.printStackTrace();
        }
        return jsonObject;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void restartLoader() {
        if (isNetworkAvailable()) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            showNoNetworkSnackbar();
        }

    }

    private void showNoNetworkSnackbar() {
        Snackbar.make(mCoordinatorLayout, R.string.no_network_snack_bar, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry_snack_bar, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartLoader();
                    }
                }).show();
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

    }

    private void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

}
