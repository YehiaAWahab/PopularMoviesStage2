package io.github.yahia_hassan.popularmoviesstage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.UriConstants;
import io.github.yahia_hassan.popularmoviesstage2.data.MovieContract;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private Context mContext;
    private FavoritesAdapterOnClickListener mFavoritesAdapterOnClickListener;
    private Cursor mCursor;

    public interface FavoritesAdapterOnClickListener {
        void OnClick(Movie movie);
    }

    public FavoritesAdapter(Context context, Cursor cursor, FavoritesAdapterOnClickListener favoritesAdapterOnClickListener) {
        mContext = context;
        mCursor = cursor;
        mFavoritesAdapterOnClickListener = favoritesAdapterOnClickListener;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FavoritesViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UriConstants.SCHEME)
                .authority(UriConstants.IMAGE_AUTHORITY)
                .appendPath(UriConstants.IMAGE_T_PATH)
                .appendPath(UriConstants.IMAGE_P_PATH)
                .appendPath(UriConstants.IMAGE_WIDTH_PATH)
                .appendEncodedPath(mCursor.getString(
                        mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)
                ));
        final String imageUrl = builder.build().toString();


        // https://stackoverflow.com/a/34051356
            Picasso.with(mContext)
            .load(imageUrl)
            .networkPolicy(NetworkPolicy.OFFLINE)
                   .into(holder.moviePoster, new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
            //Try again online if cache failed
            Picasso.with(mContext)
                    .load(imageUrl)
                    .error(R.color.placeholder_grey)
                    .into(holder.moviePoster, new Callback() {
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
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();

    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePoster;
        public FavoritesViewHolder (View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster_main_activity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = mCursor.getPosition();
            mCursor.moveToPosition(position);
            String movieId = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            String movieTitle = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            String posterPath = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            String backdropPath = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
            String plotSynopsis = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS));
            String userRating = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING));
            String releaseDate = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            Movie clickedMovie = new Movie(movieId, movieTitle, posterPath, backdropPath, plotSynopsis, userRating, releaseDate);
            mFavoritesAdapterOnClickListener.OnClick(clickedMovie);
        }
    }
}
