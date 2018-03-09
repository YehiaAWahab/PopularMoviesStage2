package io.github.yahia_hassan.popularmoviesstage2.loaders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.Helper;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.POJOs.Review;
import io.github.yahia_hassan.popularmoviesstage2.UriConstants;
import io.github.yahia_hassan.popularmoviesstage2.adapters.UserReviewAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ReviewAsyncTaskLoader implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = VideosAsyncTaskLoader.class.getSimpleName();

    private static final String RESULTS_JSON_ARRAY = "results";
    private static final String REVIEW_AUTHOR_STRING = "author";
    private static final String REVIEW_CONTENT_STRING = "content";

    private Context mContext;
    private Movie mClickedMovie;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public ReviewAsyncTaskLoader(Context context, Movie clickedMovie, RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        mContext = context;
        mClickedMovie = clickedMovie;
        mRecyclerView = recyclerView;
        mLayoutManager = layoutManager;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
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
                        .appendPath(mClickedMovie.getMovieId())
                        .appendPath(UriConstants.REVIEWS_PATH)
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
                    forceLoad();
                }


            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        JSONObject rootJSONObject = Helper.createJSONObjectFromString(TAG, data);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Review> reviewArrayList = createArrayListForUserReviews(rootJSONObject);
        UserReviewAdapter videoAdapter = new UserReviewAdapter(mContext, reviewArrayList);
        mRecyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }



    private ArrayList<Review> createArrayListForUserReviews(JSONObject rootJSONObject) {
        JSONArray resultsJSONArray = rootJSONObject.optJSONArray(RESULTS_JSON_ARRAY);

        ArrayList<Review> reviewArrayList = new ArrayList<>();
        Review review = null;
        int size = resultsJSONArray.length();

        for (int i = 0; i < size; i++) {
            JSONObject nthJSONObject = resultsJSONArray.optJSONObject(i);
            String author = nthJSONObject.optString(REVIEW_AUTHOR_STRING);
            String content = nthJSONObject.optString(REVIEW_CONTENT_STRING);

            review = new Review(author, content);
            reviewArrayList.add(review);
        }

        return reviewArrayList;
    }
}
