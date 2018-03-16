package io.github.yahia_hassan.popularmoviesstage2.loaders;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import io.github.yahia_hassan.popularmoviesstage2.DetailsActivity;
import io.github.yahia_hassan.popularmoviesstage2.FavoritesDetailsActivity;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.UriConstants;
import io.github.yahia_hassan.popularmoviesstage2.adapters.FavoritesAdapter;
import io.github.yahia_hassan.popularmoviesstage2.data.MovieContract;
import io.github.yahia_hassan.popularmoviesstage2.databinding.ActivityMainBinding;

public class MainActivityCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>,
        FavoritesAdapter.FavoritesAdapterOnClickListener {

    private Context mContext;
    private ActivityMainBinding mActivityMainBinding;

    public MainActivityCursorLoader (Context context, ActivityMainBinding activityMainBinding) {
        mContext = context;
        mActivityMainBinding = activityMainBinding;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mContext,
                Uri.parse(MovieContract.BASE_CONTENT_URI + "/" + MovieContract.PATH_MOVIES),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(mContext, data, this);
        mActivityMainBinding.recyclerView.setAdapter(favoritesAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void OnClick(Movie movie) {
        Intent intent = new Intent(mContext, FavoritesDetailsActivity.class);
        intent.putExtra(UriConstants.PARCELABLE_EXTRA_MESSAGE, movie);
        mContext.startActivity(intent);

    }
}
