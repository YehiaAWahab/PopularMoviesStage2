package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static io.github.yahia_hassan.popularmoviesstage2.MainActivity.EXTRA_MESSAGE;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        TextView mMovieTitleTextView = findViewById(R.id.original_title_tv);
        ImageView mMoviePosterImageView = findViewById(R.id.movie_poster_iv);
        TextView mPlotSynopsisTextView = findViewById(R.id.plot_synopsis_tv);
        TextView mUserRatingTextView = findViewById(R.id.user_rating_tv);
        TextView mReleaseDateTextView = findViewById(R.id.release_date_tv);


        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(MainActivity.EXTRA_MESSAGE);

        /*
         * I searched online how to set the title of the activity, found the answer on this
          * Stack Overflow answer ( https://stackoverflow.com/a/2198569/5255289 )
         */
        setTitle(movie.getMovieTitle());

        mMovieTitleTextView.setText(movie.getMovieTitle());

        String UrlFirstPart = "https://image.tmdb.org/t/p/w500/";
        Picasso.with(this)
                .load(UrlFirstPart + movie.getMoviePosterBackdrop())
                .placeholder(R.color.placeholder_grey)
                .into(mMoviePosterImageView);

        Log.d(TAG, UrlFirstPart + "Image URL is: " + movie.getMoviePosterBackdrop());

        mPlotSynopsisTextView.setText(movie.getPlotSynopsis());
        mUserRatingTextView.setText(movie.getUserRating());
        mReleaseDateTextView.setText(movie.getReleaseDate());
    }
}
