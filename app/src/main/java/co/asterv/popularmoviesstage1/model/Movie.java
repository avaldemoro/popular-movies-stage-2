package co.asterv.popularmoviesstage1.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import co.asterv.popularmoviesstage1.utils.Constants;

@Entity(tableName = "movie")
public class Movie implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int dbMovieId;
    private int movieId;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private Double voterAverage;
    private String trailerPath;
    private String reviewAuthor;
    private String reviewContents;
    private String reviewUrl;

    public Movie() { }

    // SETTER METHODS
    public void setDbMovieId(int dbMovieId) {this.dbMovieId = movieId; }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setVoterAverage(Double voterAverage) {
        this.voterAverage = voterAverage;
    }
    public void setTrailerPath(String trailerPath) { this.trailerPath = trailerPath; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public void setReviewAuthor(String reviewAuthor) { this.reviewAuthor = reviewAuthor; }
    public void setReviewContents(String reviewContents) { this.reviewContents = reviewContents; }
    public void setReviewUrl(String reviewUrl) { this.reviewUrl = reviewUrl; }

    // GETTER METHODS
    public int getDbMovieId() { return movieId; }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public String getPosterPath() {
        return Constants.MOVIEDB_IMAGE_BASE_URL + posterPath;
    }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public Double getVoterAverage() {
        return voterAverage;
    }
    public String getTrailerPath() { return trailerPath; }
    public int getMovieId() { return movieId; }
    public String getReviewAuthor() { return reviewAuthor; }
    public String getReviewContents() { return reviewContents; }
    public String getReviewUrl() { return reviewUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(voterAverage);
        dest.writeString(trailerPath);
        dest.writeInt (movieId);
        dest.writeString (reviewAuthor);
        dest.writeString (reviewContents);
        dest.writeString (reviewUrl);
    }

    @Ignore
    private Movie(Parcel parcel) {
        originalTitle = parcel.readString();
        posterPath = parcel.readString();
        overview = parcel.readString();
        releaseDate = parcel.readString();
        voterAverage = parcel.readDouble();
        trailerPath = parcel.readString();
        movieId = parcel.readInt ();
        reviewAuthor = parcel.readString ();
        reviewContents = parcel.readString ();
        reviewUrl = parcel.readString ();
    }

    @Ignore
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel src) {
            return new Movie(src);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
