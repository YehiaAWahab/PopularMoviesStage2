package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.adapters.PopularMoviesAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        PopularMoviesAdapter.MovieAdapterOnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final int LOADER_ID = 45;


    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private GridLayoutManager mLayoutManager;
    private TextView mNoNetworkTextView;
    private Button mRetryButton;
    private ProgressBar mProgressBar;
    private Bundle mBundle;


    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String MOVIE_ID_STRING = "id";
    private static final String MOVIE_ORIGINAL_TITLE_STRING = "original_title";
    private static final String MOVIE_POSTER_PATH_STRING = "poster_path";
    private static final String MOVIE_Backdrop_POSTER_PATH_STRING = "backdrop_path";
    private static final String MOVIE_PLOT_SYNOPSIS_STRING = "overview";
    private static final String MOVIE_USER_RATING_STRING = "vote_average";
    private static final String MOVIE_RELEASE_DATE_STRING = "release_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoNetworkTextView = findViewById(R.id.no_network_tv);
        mRetryButton = findViewById(R.id.retry_button);
        mRecyclerView = findViewById(R.id.recycler_view);
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


        if (Helper.isNetworkAvailable(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.POPULAR_PATH);
            getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
        } else {
            showNoNetworkError();
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.POPULAR_PATH);
                restartLoader(bundle);
            }
        });

    }

    @Override
    public void OnClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(UriConstants.EXTRA_MESSAGE, movie);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        mBundle = args;
        return new AsyncTaskLoader<String>(this) {
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
                        .appendPath(mBundle.getString(getString(R.string.main_activity_bundle_key)))
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_by_menu_popular:
                bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.POPULAR_PATH);
                restartLoader(bundle);
                return true;
            case R.id.sort_by_menu_top_rated:
                bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.TOP_RATED_PATH);
                restartLoader(bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void restartLoader(Bundle bundle) {
        if (Helper.isNetworkAvailable(this)) {
            getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
        } else {
            showNoNetworkError();
        }

    }

    private void showNoNetworkError() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mNoNetworkTextView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mNoNetworkTextView.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);

    }

    private void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mNoNetworkTextView.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }

}
