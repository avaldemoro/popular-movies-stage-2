package co.asterv.popularmoviesstage1;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import co.asterv.popularmoviesstage1.database.AppDatabase;
import co.asterv.popularmoviesstage1.model.Movie;
import co.asterv.popularmoviesstage1.utils.Constants;
import co.asterv.popularmoviesstage1.utils.JsonUtils;

public class MovieDetails extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ReviewAdapter mReviewAdapter;
    TextView reviewLabel;
    View divider;
    Movie movie;
    ToggleButton favoriteBtn;
    private AppDatabase mDb;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_movie_details);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar ();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        Intent intent = getIntent();
        if (intent == null) { closeOnError(); }

        movie = intent.getParcelableExtra("movie");
        mDb = AppDatabase.getInstance (getApplicationContext ());

        // All movies saved to phone are "Favorite movies"
        final Movie[] favoriteMovies = mDb.movieDao ().loadAllMovies ();

        // So, if the current movieId matches any one of the movies in favoriteMovies
        // set isFavoriteMovie to true
        for(int i = 0; i < favoriteMovies.length; i++) {
            if (movie.getMovieId () == favoriteMovies[i].getMovieId ()) {
                movie.setIsFavoriteMovie (true);
            }
        }

        setupDetailsUI (movie);

        // TOGGLE TOGGLE TOGGLE
        favoriteBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled

            } else {
                // The toggle is disabled
                favoriteBtn.setTextColor (Color.parseColor("#000000"));
                favoriteBtn.getTextOff();

                movie.setIsFavoriteMovie (false);
                AppExecutor.getInstance().diskIO().execute(() -> mDb.movieDao().deleteMovie (movie));
                Log.e("fav movie after delete?", String.valueOf(movie.getOriginalTitle ()) + String.valueOf(movie.getIsFavoriteMovie ()));
            }
        });
        //TOGGLE BUTTON
        favoriteBtn.setOnClickListener(v -> onFavoriteButtonClicked ());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
    }

    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState (savedState);
    }

    public boolean onOptionsSelectedItem(MenuItem item) {
        int id = item.getItemId ();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask (this);
        }
        return super.onOptionsItemSelected (item);
    }

    void closeOnError() {
        finish();
        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
    }

    private void setupDetailsUI(Movie movie) {
        TextView originalTitleTV = findViewById (R.id.titleTextView);
        TextView ratingTV = findViewById (R.id.ratingTextView);
        TextView releaseDateTV = findViewById (R.id.releaseDateTextView);
        TextView overviewTV = findViewById (R.id.overviewTextView);
        ImageView posterIV = findViewById (R.id.posterImageView);
        Button trailerBtn = findViewById (R.id.watchTrailerBtn);
        favoriteBtn = findViewById (R.id.favoritesBtn);

        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.reviewsRecyclerView);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // TITLE
        originalTitleTV.setText(movie.getOriginalTitle());

        // VOTER AVERAGE / RATING
        ratingTV.setText (String.valueOf(movie.getVoterAverage ()) + Constants.OUT_OF_RATING_STRING);

        // IMAGE
        Picasso.with(this)
                .load(movie.getPosterPath())
                .fit ().centerCrop ()
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(posterIV);

        // OVERVIEW
        overviewTV.setText (movie.getOverview ());

        // RELEASE DATE
        releaseDateTV.setText (movie.getReleaseDate());

        // TRAILER BUTTON
        new TrailerButtonAsyncTask (trailerBtn).execute(String.valueOf(movie.getMovieId ()), Constants.VIDEO_QUERY_PARAM);

        // LOAD REVIEWS
        new ReviewsAsyncTask ().execute(String.valueOf(movie.getMovieId ()), Constants.REVIEW_URL_QUERY_PARAM);

        // INITIAL BUTTON VALUES
        favoriteBtn.setTextOn(Constants.FAVORITED_STRING);
        favoriteBtn.setTextOff(Constants.ADD_TO_FAVORITES_STRING);
        Log.e("IS FAVORITE MOVIE?!?!", String.valueOf (movie.getIsFavoriteMovie ()));
        if(movie.getIsFavoriteMovie ()) {
            favoriteBtn.setChecked (true);
            favoriteBtn.setText(Constants.FAVORITED_STRING);
            favoriteBtn.setTextColor (Color.parseColor("#b5001e"));
        } else {
            favoriteBtn.setTextColor (Color.parseColor("#000000"));
            favoriteBtn.setChecked (false);
            favoriteBtn.setText(Constants.ADD_TO_FAVORITES_STRING);
        }
    }

    /*** ASYNC TASK FOR THE "WATCH TRAILER" BUTTON ***/
    private class TrailerButtonAsyncTask extends AsyncTask<String, Void, String> {
        private final Button button;
        String trailerKey = null;

        public TrailerButtonAsyncTask(Button button) {
            this.button = button;
        }

        @Override
        protected String doInBackground(String... strings) {
            Movie[] movies;
            try {
                URL url = JsonUtils.buildMovieIdUrl(strings[0], strings[1]);
                String movieSearchResults = JsonUtils.getResponseFromHttpUrl(url);

                JSONObject root = new JSONObject(movieSearchResults);
                JSONArray resultsArray = root.getJSONArray (Constants.RESULTS_QUERY_PARAM);
                movies = new Movie[resultsArray.length ()];

                for (int i = 0; i < resultsArray.length(); i++) {
                    // Initialize each object before it can be used
                    movies[i] = new Movie();

                    // Object contains all tags we're looking for
                    JSONObject movieInfo = resultsArray.getJSONObject(i);

                    // Store data in movie object
                    movies[i].setTrailerPath(movieInfo.getString(Constants.VIDEO_TRAILER_KEY_PARAM));
                }
                // Returns only the first trailer from the results array, since there can be multiple trailers
                return movies[0].getTrailerPath();

            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            return trailerKey;
        }

        protected void onPostExecute(String temp) {
            button.setOnClickListener((View v) -> {
                watchYoutubeVideo (getApplicationContext (), temp);
            });
        }
    }

    /*** YT VIDEO INTENT. https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent ***/
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_APP_BASE + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(Constants.YOUTUBE_BASE_URL + id));
        // If youtube is not installed, plays from web
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    /*** ASYNC TASK TO FETCH REVIEWS ***/
    private class ReviewsAsyncTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String... strings) {
            try {
                URL url = JsonUtils.buildMovieIdUrl(strings[0], strings[1]);
                String movieSearchResults = JsonUtils.getResponseFromHttpUrl(url);
                return setMovieDataToArray(movieSearchResults);
            } catch (IOException | JSONException e) {
                e.printStackTrace ();
            }
            return null;
        }
        protected void onPostExecute(Movie[] movies) {
            // specify an adapter
            mReviewAdapter = new ReviewAdapter(movies, getApplicationContext ());
            if(mReviewAdapter.getItemCount () == -1) {
                // If there's no reviews, make the label and divider for the reviews visibility to none
                reviewLabel = findViewById (R.id.textView);
                divider = findViewById (R.id.divider2);
                reviewLabel.setVisibility (TextView.GONE);
                divider.setVisibility (View.GONE);
            } else {
                mRecyclerView.setAdapter(mReviewAdapter);
            }

        }
    }

    /*** ADD REVIEW DATA TO MOVIE OBJECT ***/
    public Movie[] setMovieDataToArray(String jsonResults) throws JSONException {
        JSONObject root = new JSONObject(jsonResults);
        JSONArray resultsArray = root.getJSONArray (Constants.RESULTS_QUERY_PARAM);
        Movie[] movies = new Movie[resultsArray.length ()];

        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setReviewAuthor (movieInfo.getString(Constants.REVIEW_AUTHOR_QUERY_PARAM));
            movies[i].setReviewContents (movieInfo.getString(Constants.REVIEW_QUERY_PARAM));
            movies[i].setReviewUrl (movieInfo.getString (Constants.REVIEW_URL_PARAM));
        }
        return movies;
    }

    /*** FAVORITE MOVIE BUTTON IS CALLED WHEN "ADD TO FAVORITES" BUTTON IS CLICKED***/
    public void onFavoriteButtonClicked() {
        final Movie movie = getIntent().getExtras().getParcelable ( "movie");
        AppExecutor.getInstance ().diskIO ().execute (() -> {
            runOnUiThread (() -> {
                mDb.movieDao ().insertMovie (movie);
                //finish ();
            });
        });
    }
}