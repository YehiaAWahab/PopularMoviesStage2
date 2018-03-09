package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.yahia_hassan.popularmoviesstage2.loaders.ReviewAsyncTaskLoader;
import io.github.yahia_hassan.popularmoviesstage2.loaders.VideosAsyncTaskLoader;

public class DetailsActivity extends AppCompatActivity {



    public static final int REVIEW_LOADER_ID = 40;
    public static final int VIDEO_LOADER_ID = 41;

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (Helper.isNetworkAvailable(this)) {

            Movie mClickedMovie;

            TextView mMovieTitleTextView;
            ImageView mMoviePosterImageView;
            TextView mPlotSynopsisTextView;
            TextView mUserRatingTextView;
            TextView mReleaseDateTextView;


            RecyclerView mUserReviewRecyclerView;
            RecyclerView mVideosRecyclerView;
            LinearLayoutManager mUserReviewLinearLayoutManager;
            LinearLayoutManager mVideoLinearLayoutManager;


            mMovieTitleTextView = findViewById(R.id.original_title_tv);
            mMoviePosterImageView = findViewById(R.id.movie_poster_iv);
            mPlotSynopsisTextView = findViewById(R.id.plot_synopsis_tv);
            mUserRatingTextView = findViewById(R.id.user_rating_tv);
            mReleaseDateTextView = findViewById(R.id.release_date_tv);

            mUserReviewRecyclerView = findViewById(R.id.users_review_recycler_view);
            mVideosRecyclerView = findViewById(R.id.videos_recycler_view);

            mUserReviewLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mVideoLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


            Intent intent = getIntent();
            mClickedMovie = intent.getParcelableExtra(UriConstants.EXTRA_MESSAGE);



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
    }
}
