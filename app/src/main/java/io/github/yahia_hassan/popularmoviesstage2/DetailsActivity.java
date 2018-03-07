package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String API_KEY = BuildConfig.API_KEY;

    public static final int REVIEW_LOADER_ID = 40;
    public static final int VIDEO_LOADER_ID = 41;

    private Movie mClickedMovie;
    private Bundle mBundle;

    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mPlotSynopsisTextView;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mUserReviewTextView;

    private TextView mPlotSynopsisLabelTextView;
    private TextView mReleaseDateLabelTextView;
    private TextView mUserRatingLabelTextView;
    private TextView mUserReviewLablTextView;

    private ProgressBar mProgressBar;



    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String REVIEW_AUTHOR_STRING = "author";
    private static final String REVIEW_CONTENT_STRING = "content";



    private static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitleTextView = findViewById(R.id.original_title_tv);
        mMoviePosterImageView = findViewById(R.id.movie_poster_iv);
        mPlotSynopsisTextView = findViewById(R.id.plot_synopsis_tv);
        mUserRatingTextView = findViewById(R.id.user_rating_tv);
        mReleaseDateTextView = findViewById(R.id.release_date_tv);
        mUserReviewTextView = findViewById(R.id.user_review_tv);

        mPlotSynopsisLabelTextView = findViewById(R.id.plot_synopsis_label);
        mReleaseDateLabelTextView = findViewById(R.id.release_date_label);
        mUserRatingLabelTextView = findViewById(R.id.user_rating_label);
        mUserReviewLablTextView = findViewById(R.id.user_review_label);

        mProgressBar = findViewById(R.id.details_progress_bar);

        if (!Helper.isNetworkAvailable(this)) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.details_activity_bundle_key), UriConstants.REVIEWS_PATH);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, bundle, this);

        Intent intent = getIntent();
        mClickedMovie = intent.getParcelableExtra(UriConstants.EXTRA_MESSAGE);

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
                        .appendPath(mClickedMovie.getMovieId())
                        .appendPath(mBundle.getString(getString(R.string.details_activity_bundle_key)))
                        .appendQueryParameter(UriConstants.API_KEY_QUERY_PARAM, API_KEY);
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
        showData();
        JSONObject rootJSONObject = Helper.createJSONObjectFromString(TAG, data);
        JSONArray resultsJSONArray = rootJSONObject.optJSONArray(RESULTS_JSON_ARRAY);
        int size = resultsJSONArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject nthJSONObject = resultsJSONArray.optJSONObject(i);
            String author = nthJSONObject.optString(REVIEW_AUTHOR_STRING);
            String content = nthJSONObject.optString(REVIEW_CONTENT_STRING);

            String finalResult = "<b>" + author + "</b>" + ":" + "<br>" + content + "<br><br><br>";

            mUserReviewTextView.append(Html.fromHtml(finalResult));
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        getSupportLoaderManager().destroyLoader(loader.getId());
    }


    private void showProgressBar() {
        mMovieTitleTextView.setVisibility(View.GONE);
        mMoviePosterImageView.setVisibility(View.GONE);
        mPlotSynopsisTextView.setVisibility(View.GONE);
        mUserRatingTextView.setVisibility(View.GONE);
        mReleaseDateTextView.setVisibility(View.GONE);
        mUserReviewTextView.setVisibility(View.GONE);

        mPlotSynopsisLabelTextView.setVisibility(View.GONE);
        mReleaseDateLabelTextView.setVisibility(View.GONE);
        mUserRatingLabelTextView.setVisibility(View.GONE);
        mUserReviewLablTextView.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showData() {
        mMovieTitleTextView.setVisibility(View.VISIBLE);
        mMoviePosterImageView.setVisibility(View.VISIBLE);
        mPlotSynopsisTextView.setVisibility(View.VISIBLE);
        mUserRatingTextView.setVisibility(View.VISIBLE);
        mReleaseDateTextView.setVisibility(View.VISIBLE);
        mUserReviewTextView.setVisibility(View.VISIBLE);

        mPlotSynopsisLabelTextView.setVisibility(View.VISIBLE);
        mReleaseDateLabelTextView.setVisibility(View.VISIBLE);
        mUserRatingLabelTextView.setVisibility(View.VISIBLE);
        mUserReviewLablTextView.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.GONE);
    }


}
