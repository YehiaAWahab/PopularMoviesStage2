package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
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

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.adapters.FavoritesAdapter;
import io.github.yahia_hassan.popularmoviesstage2.adapters.PopularMoviesAdapter;
import io.github.yahia_hassan.popularmoviesstage2.data.MovieContract;
import io.github.yahia_hassan.popularmoviesstage2.databinding.ActivityMainBinding;
import io.github.yahia_hassan.popularmoviesstage2.loaders.MainActivityAsyncTaskLoader;
import io.github.yahia_hassan.popularmoviesstage2.loaders.MainActivityCursorLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final int MAIN_LOADER_ID = 45;
    public static final int FAVORITES_LOADER_ID = 46;



    private GridLayoutManager mLayoutManager;

    private ActivityMainBinding mActivityMainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        /*
         * I search online how to get the Activity orientation and find the solution here
          * on Stack Overflow ( https://stackoverflow.com/a/11381854/5255289 )
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 3);
        } else {
            mLayoutManager = new GridLayoutManager(this, 2);
        }


        mActivityMainBinding.recyclerView.setLayoutManager(mLayoutManager);


        if (Helper.isNetworkAvailable(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.POPULAR_PATH);
            MainActivityAsyncTaskLoader mainActivityAsyncTaskLoader = new MainActivityAsyncTaskLoader(this, mActivityMainBinding);
            getSupportLoaderManager().initLoader(MAIN_LOADER_ID, bundle, mainActivityAsyncTaskLoader);
        } else {
            showNoNetworkError();
        }

        mActivityMainBinding.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.main_activity_bundle_key), UriConstants.POPULAR_PATH);
                restartLoader(bundle);
            }
        });

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
            case R.id.sort_by_menu_favorite:
                MainActivityCursorLoader mainActivityCursorLoader = new MainActivityCursorLoader(this, mActivityMainBinding);
                getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, mainActivityCursorLoader);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void restartLoader(Bundle bundle) {
        if (Helper.isNetworkAvailable(this)) {
            MainActivityAsyncTaskLoader mainActivityAsyncTaskLoader = new MainActivityAsyncTaskLoader(this, mActivityMainBinding);
            getSupportLoaderManager().restartLoader(MAIN_LOADER_ID, bundle, mainActivityAsyncTaskLoader);
        } else {
            showNoNetworkError();
        }

    }

    private void showNoNetworkError() {
        mActivityMainBinding.progressBar.setVisibility(View.GONE);
        mActivityMainBinding.recyclerView.setVisibility(View.GONE);
        mActivityMainBinding.noNetworkTv.setVisibility(View.VISIBLE);
        mActivityMainBinding.retryButton.setVisibility(View.VISIBLE);
    }



}
