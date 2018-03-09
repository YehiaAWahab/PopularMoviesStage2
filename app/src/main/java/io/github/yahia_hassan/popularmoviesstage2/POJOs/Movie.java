package io.github.yahia_hassan.popularmoviesstage2.POJOs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a movie
 */

public class Movie implements Parcelable {
    private String mMovieId;
    private String mMovieTitle;
    private String mMoviePoster;
    private String mMoviePosterBackdrop;
    private String mPlotSynopsis;
    private String mUserRating;
    private String mReleaseDate;

    public Movie(String movieId, String movieTitle, String moviePoster, String moviePosterBackdrop, String plotSynopsis, String userRating, String releaseDate) {
        mMovieId = movieId;
        mMovieTitle = movieTitle;
        mMoviePoster = moviePoster;
        mMoviePosterBackdrop = moviePosterBackdrop;
        mPlotSynopsis = plotSynopsis;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    public Movie(Parcel parcel) {
        setMovieId(parcel.readString());
        setMovieTitle(parcel.readString());
        setMoviePoster(parcel.readString());
        setMoviePosterBackdrop(parcel.readString());
        setPlotSynopsis(parcel.readString());
        setUserRating(parcel.readString());
        setReleaseDate(parcel.readString());
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMovieId());
        dest.writeString(getMovieTitle());
        dest.writeString(getMoviePoster());
        dest.writeString(getMoviePosterBackdrop());
        dest.writeString(getPlotSynopsis());
        dest.writeString(getUserRating());
        dest.writeString(getReleaseDate());

    }




    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setMovieTitle(String movieTitle) {
        mMovieTitle = movieTitle;
    }

    public void setMoviePoster(String moviePoster) {
        mMoviePoster = moviePoster;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        mPlotSynopsis = plotSynopsis;
    }

    public void setUserRating(String userRating) {
        mUserRating = userRating;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getMoviePosterBackdrop() {
        return mMoviePosterBackdrop;
    }

    public void setMoviePosterBackdrop(String moviePosterBackdrop) {
        mMoviePosterBackdrop = moviePosterBackdrop;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }
}
