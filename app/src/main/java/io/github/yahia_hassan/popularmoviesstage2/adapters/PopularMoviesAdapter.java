package io.github.yahia_hassan.popularmoviesstage2.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.yahia_hassan.popularmoviesstage2.POJOs.Movie;
import io.github.yahia_hassan.popularmoviesstage2.R;
import io.github.yahia_hassan.popularmoviesstage2.UriConstants;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {

    private static final String TAG = PopularMoviesAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieArrayList;
    private Context mContext;
    private MovieAdapterOnClickListener mMovieAdapterOnClickListener;


    public interface MovieAdapterOnClickListener {
        void OnClick(Movie movie);
    }

    public PopularMoviesAdapter (Context context, ArrayList<Movie> movieArrayList, MovieAdapterOnClickListener movieAdapterOnClickListener) {
        mMovieArrayList = movieArrayList;
        mContext = context;
        mMovieAdapterOnClickListener = movieAdapterOnClickListener;
    }



    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new PopularMoviesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        Movie movie = mMovieArrayList.get(position);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(UriConstants.SCHEME)
                .authority(UriConstants.IMAGE_AUTHORITY)
                .appendPath(UriConstants.IMAGE_T_PATH)
                .appendPath(UriConstants.IMAGE_P_PATH)
                .appendPath(UriConstants.IMAGE_WIDTH_PATH)
                .appendEncodedPath(movie.getMoviePoster());
        String imageUrl = builder.build().toString();
        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.color.placeholder_grey)
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return mMovieArrayList.size();
    }


    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePoster;
        public PopularMoviesViewHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster_main_activity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie clickedMovie = mMovieArrayList.get(position);
            mMovieAdapterOnClickListener.OnClick(clickedMovie);
        }
    }
}
