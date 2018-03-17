package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Video;
import io.github.yahia_hassan.popularmoviesstage2.databinding.ActivityDetailsBinding;
import io.github.yahia_hassan.popularmoviesstage2.loaders.ReviewAsyncTaskLoader;
import io.github.yahia_hassan.popularmoviesstage2.loaders.VideosAsyncTaskLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {



    public static final int REVIEW_LOADER_ID = 40;
    public static final int VIDEO_LOADER_ID = 41;

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private final int SHARE_LOADER_ID = 84;
    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String VIDEO_KEY_STRING = "key";
    private static final String VIDEO_NAME_STRING = "name";

    private Movie mClickedMovie;

    private String mFirstTrailerTitle;
    private String mFirstTrailerURL;

    private LinearLayoutManager mUserReviewLinearLayoutManager;
    private LinearLayoutManager mVideoLinearLayoutManager;
    ActivityDetailsBinding mActivityDetailsBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mActivityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        Intent intent = getIntent();

        mUserReviewLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mClickedMovie = intent.getParcelableExtra(UriConstants.PARCELABLE_EXTRA_MESSAGE);

        mActivityDetailsBinding.setMovie(mClickedMovie);




        if (Helper.isMovieInFavoritesList(this, mClickedMovie)) {
            mActivityDetailsBinding.favoriteFab.setImageResource(R.drawable.ic_favorite);
        }

        if (Helper.isNetworkAvailable(this)) {

            makeNetworkRequest();

        } else {
            showNoNetworkError();
        }

        mActivityDetailsBinding.detailsActivityRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartLoaders();
            }
        });

        mActivityDetailsBinding.favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.addAndDeleteMovieFromTheDatabase(getBaseContext(), mClickedMovie, mActivityDetailsBinding.favoriteFab);
            }
        });
    }


    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            String mVideosJSON;

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
                        .appendPath(mClickedMovie.getMovieId())
                        .appendPath(UriConstants.VIDEOS_PATH)
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
                mVideosJSON = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (mVideosJSON != null) {
                    deliverResult(mVideosJSON);
                } else {
                    forceLoad();
                }


            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        JSONObject jsonObject = Helper.createJSONObjectFromString(TAG, data);
        JSONArray jsonArray = jsonObject.optJSONArray(RESULTS_JSON_ARRAY);
        JSONObject firstTrailerJSONObject = jsonArray.optJSONObject(0);
        mFirstTrailerTitle = firstTrailerJSONObject.optString(VIDEO_NAME_STRING);
        String key = firstTrailerJSONObject.optString(VIDEO_KEY_STRING);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UriConstants.YOUTUBE_SCHEME)
                .authority(UriConstants.YOUTUBE_AUTHORITY)
                .appendPath(UriConstants.YOUTUBE_WATCH_PATH)
                .appendQueryParameter(UriConstants.YOUTUBE_V_QUERY_PARAMETER, key);
        mFirstTrailerURL = builder.build().toString();


    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }


    private void restartLoaders() {
        if (Helper.isNetworkAvailable(this)) {
            showData();
            makeNetworkRequest();
            getSupportLoaderManager().restartLoader(SHARE_LOADER_ID, null, this);
        } else {
            showNoNetworkError();
        }
    }

    private void makeNetworkRequest() {
        VideosAsyncTaskLoader videosAsyncTaskLoader = new VideosAsyncTaskLoader(this,
                mClickedMovie,
                mActivityDetailsBinding.videosRecyclerView,
                mVideoLinearLayoutManager);

        getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, videosAsyncTaskLoader);


        ReviewAsyncTaskLoader reviewAsyncTaskLoader = new ReviewAsyncTaskLoader(this,
                mClickedMovie,
                mActivityDetailsBinding.usersReviewRecyclerView,
                mUserReviewLinearLayoutManager);

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewAsyncTaskLoader);

        getSupportLoaderManager().initLoader(SHARE_LOADER_ID, null, this);


        /*
         * I searched online how to set the title of the activity, found the answer on this
          * Stack Overflow answer ( https://stackoverflow.com/a/2198569/5255289 )
         */
        setTitle(mActivityDetailsBinding.getMovie().getMovieTitle());



        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UriConstants.SCHEME)
                .authority(UriConstants.IMAGE_AUTHORITY)
                .appendPath(UriConstants.IMAGE_T_PATH)
                .appendPath(UriConstants.IMAGE_P_PATH)
                .appendPath(UriConstants.IMAGE_WIDTH_PATH)
                .appendEncodedPath(mClickedMovie.getMoviePosterBackdrop());
        String url = builder.build().toString();

        Picasso.with(this)
                .load(url)
                .placeholder(R.color.placeholder_grey)
                .into(mActivityDetailsBinding.moviePosterIv);


    }

    private void showNoNetworkError() {
        mActivityDetailsBinding.originalTitleTv.setVisibility(View.GONE);
        mActivityDetailsBinding.moviePosterIv.setVisibility(View.GONE);
        mActivityDetailsBinding.plotSynopsisTv.setVisibility(View.GONE);
        mActivityDetailsBinding.userRatingTv.setVisibility(View.GONE);
        mActivityDetailsBinding.releaseDateTv.setVisibility(View.GONE);


        mActivityDetailsBinding.usersReviewRecyclerView.setVisibility(View.GONE);
        mActivityDetailsBinding.videosRecyclerView.setVisibility(View.GONE);


        mActivityDetailsBinding.userRatingLabel.setVisibility(View.GONE);
        mActivityDetailsBinding.releaseDateLabel.setVisibility(View.GONE);
        mActivityDetailsBinding.plotSynopsisLabel.setVisibility(View.GONE);
        mActivityDetailsBinding.videosLabel.setVisibility(View.GONE);
        mActivityDetailsBinding.userReviewLabel.setVisibility(View.GONE);

        mActivityDetailsBinding.detailsActivityNoConnectionTv.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.detailsActivityRetryButton.setVisibility(View.VISIBLE);
    }

    private void showData() {
        mActivityDetailsBinding.originalTitleTv.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.moviePosterIv.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.plotSynopsisTv.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.userRatingTv.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.releaseDateTv.setVisibility(View.VISIBLE);


        mActivityDetailsBinding.usersReviewRecyclerView.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.videosRecyclerView.setVisibility(View.VISIBLE);


        mActivityDetailsBinding.userRatingLabel.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.releaseDateLabel.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.plotSynopsisLabel.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.videosLabel.setVisibility(View.VISIBLE);
        mActivityDetailsBinding.userReviewLabel.setVisibility(View.VISIBLE);

        mActivityDetailsBinding.detailsActivityNoConnectionTv.setVisibility(View.GONE);
        mActivityDetailsBinding.detailsActivityRetryButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details_activity_menu:
                if (mFirstTrailerURL != null && mFirstTrailerTitle != null) {
                    Intent intent = ShareCompat.IntentBuilder.from(this)
                            .setType("text/plain")
                            .setText(getString(R.string.details_activity_share, mClickedMovie.getMovieTitle(), mFirstTrailerTitle, mFirstTrailerURL))
                            .getIntent();
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
