package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.databinding.ActivityFavoritesDetailsBinding;

public class FavoritesDetailsActivity extends AppCompatActivity {
    private static final String TAG = FavoritesDetailsActivity.class.getSimpleName();

    private Movie mClickedMovie;
    private ActivityFavoritesDetailsBinding mActivityFavoritesDetailsBinding;
    private String mBackdropImagePath;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_details);

        mActivityFavoritesDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites_details);
        mContext = this;

        Intent intent = getIntent();
        mClickedMovie = intent.getParcelableExtra(UriConstants.PARCELABLE_EXTRA_MESSAGE);
        mActivityFavoritesDetailsBinding.setFavoritesMovie(mClickedMovie);
        mActivityFavoritesDetailsBinding.fdaFavoriteFab.setImageResource(R.drawable.ic_favorite);

        setTitle(mClickedMovie.getMovieTitle());
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UriConstants.SCHEME)
                .authority(UriConstants.IMAGE_AUTHORITY)
                .appendPath(UriConstants.IMAGE_T_PATH)
                .appendPath(UriConstants.IMAGE_P_PATH)
                .appendPath(UriConstants.IMAGE_WIDTH_PATH)
                .appendEncodedPath(mClickedMovie.getMoviePosterBackdrop());
        mBackdropImagePath = builder.build().toString();
        Log.d(TAG, "Favorites Details Activity Image URL: " + mBackdropImagePath);

        // https://stackoverflow.com/a/34051356
        Picasso.with(mContext)
                .load(mBackdropImagePath)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mActivityFavoritesDetailsBinding.fdaMoviePosterIv, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(mBackdropImagePath)
                                .error(R.color.placeholder_grey)
                                .into(mActivityFavoritesDetailsBinding.fdaMoviePosterIv, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("FavoriteAdapter Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });

        mActivityFavoritesDetailsBinding.fdaFavoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.addAndDeleteMovieFromTheDatabase(getBaseContext(), mClickedMovie, mActivityFavoritesDetailsBinding.fdaFavoriteFab);
            }
        });
    }
}
