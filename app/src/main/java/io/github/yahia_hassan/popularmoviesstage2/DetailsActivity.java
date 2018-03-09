package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.loaders.ReviewAsyncTaskLoader;
import io.github.yahia_hassan.popularmoviesstage2.loaders.VideosAsyncTaskLoader;

public class DetailsActivity extends AppCompatActivity {



    public static final int REVIEW_LOADER_ID = 40;
    public static final int VIDEO_LOADER_ID = 41;

    private static final String TAG = DetailsActivity.class.getSimpleName();


    Movie mClickedMovie;

    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mPlotSynopsisTextView;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;

    private TextView mUserRatingLabelTextView;
    private TextView mReleaseDateLabelTextView;
    private TextView mPlotSynopsisLabelTextView;
    private TextView mVideosLabelTextView;
    private TextView mUserReviewsLabelTextView;


    private RecyclerView mUserReviewRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private LinearLayoutManager mUserReviewLinearLayoutManager;
    private LinearLayoutManager mVideoLinearLayoutManager;

    private TextView mNoNetworkTextView;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        mMovieTitleTextView = findViewById(R.id.original_title_tv);
        mMoviePosterImageView = findViewById(R.id.movie_poster_iv);
        mPlotSynopsisTextView = findViewById(R.id.plot_synopsis_tv);
        mUserRatingTextView = findViewById(R.id.user_rating_tv);
        mReleaseDateTextView = findViewById(R.id.release_date_tv);

        mUserRatingLabelTextView = findViewById(R.id.user_rating_label);
        mReleaseDateLabelTextView = findViewById(R.id.release_date_label);
        mPlotSynopsisLabelTextView = findViewById(R.id.plot_synopsis_label);
        mVideosLabelTextView = findViewById(R.id.videos_label);
        mUserReviewsLabelTextView = findViewById(R.id.user_review_label);

        mUserReviewRecyclerView = findViewById(R.id.users_review_recycler_view);
        mVideosRecyclerView = findViewById(R.id.videos_recycler_view);

        mNoNetworkTextView = findViewById(R.id.details_activityno_network_tv);
        mRetryButton = findViewById(R.id.details_activity_retry_button);

        mUserReviewLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        Intent intent = getIntent();
        mClickedMovie = intent.getParcelableExtra(UriConstants.EXTRA_MESSAGE);

        if (Helper.isNetworkAvailable(this)) {

            makeNetworkRequest();

        } else {
            showNoNetworkError();
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartLoaders();
            }
        });
    }



    private void restartLoaders() {
        if (Helper.isNetworkAvailable(this)) {
            showData();
            makeNetworkRequest();
        } else {
            showNoNetworkError();
        }
    }

    private void makeNetworkRequest() {
        VideosAsyncTaskLoader videosAsyncTaskLoader = new VideosAsyncTaskLoader(this, mClickedMovie, mVideosRecyclerView, mVideoLinearLayoutManager);
        getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, videosAsyncTaskLoader);

        ReviewAsyncTaskLoader reviewAsyncTaskLoader = new ReviewAsyncTaskLoader(this, mClickedMovie, mUserReviewRecyclerView, mUserReviewLinearLayoutManager);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewAsyncTaskLoader);

        /*
         * I searched online how to set the title of the activity, found the answer on this
          * Stack Overflow answer ( https://stackoverflow.com/a/2198569/5255289 )
         */
        setTitle(mClickedMovie.getMovieTitle());

        mMovieTitleTextView.setText(mClickedMovie.getMovieTitle());

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
                .into(mMoviePosterImageView);

        Log.d(TAG, "Image URL is: " + url);

        mPlotSynopsisTextView.setText(mClickedMovie.getPlotSynopsis());
        mUserRatingTextView.setText(mClickedMovie.getUserRating());
        mReleaseDateTextView.setText(mClickedMovie.getReleaseDate());
    }

    private void showNoNetworkError() {
        mMovieTitleTextView.setVisibility(View.GONE);
        mMoviePosterImageView.setVisibility(View.GONE);
        mPlotSynopsisTextView.setVisibility(View.GONE);
        mUserRatingTextView.setVisibility(View.GONE);
        mReleaseDateTextView.setVisibility(View.GONE);
        mUserReviewRecyclerView.setVisibility(View.GONE);
        mVideosRecyclerView.setVisibility(View.GONE);

        mUserRatingLabelTextView.setVisibility(View.GONE);
        mReleaseDateLabelTextView.setVisibility(View.GONE);
        mPlotSynopsisLabelTextView.setVisibility(View.GONE);
        mVideosLabelTextView.setVisibility(View.GONE);
        mUserReviewsLabelTextView.setVisibility(View.GONE);

        mNoNetworkTextView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void showData() {
        mMovieTitleTextView.setVisibility(View.VISIBLE);
        mMoviePosterImageView.setVisibility(View.VISIBLE);
        mPlotSynopsisTextView.setVisibility(View.VISIBLE);
        mUserRatingTextView.setVisibility(View.VISIBLE);
        mReleaseDateTextView.setVisibility(View.VISIBLE);
        mUserReviewRecyclerView.setVisibility(View.VISIBLE);
        mVideosRecyclerView.setVisibility(View.VISIBLE);

        mUserRatingLabelTextView.setVisibility(View.VISIBLE);
        mReleaseDateLabelTextView.setVisibility(View.VISIBLE);
        mPlotSynopsisLabelTextView.setVisibility(View.VISIBLE);
        mVideosLabelTextView.setVisibility(View.VISIBLE);
        mUserReviewsLabelTextView.setVisibility(View.VISIBLE);

        mNoNetworkTextView.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }

}
