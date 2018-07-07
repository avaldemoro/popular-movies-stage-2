package co.asterv.popularmoviesstage1;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import co.asterv.popularmoviesstage1.model.Movie;
import co.asterv.popularmoviesstage1.utils.Constants;
import co.asterv.popularmoviesstage1.utils.JsonUtils;

public class MovieDetails extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_movie_details);
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar ();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie movie = intent.getParcelableExtra("movie");

        mRecyclerView = (RecyclerView) findViewById(R.id.reviewsRecyclerView);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager (this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setupDetailsUI (movie);

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

    @SuppressLint("SetTextI18n")
    private void setupDetailsUI(Movie movie) {
        TextView originalTitleTV = findViewById (R.id.titleTextView);
        TextView ratingTV = findViewById (R.id.ratingTextView);
        TextView releaseDateTV = findViewById (R.id.releaseDateTextView);
        TextView overviewTV = findViewById (R.id.overviewTextView);
        ImageView posterIV = findViewById (R.id.posterImageView);
        Button trailerBtn = findViewById (R.id.watchTrailerBtn);

        // TITLE
        originalTitleTV.setText(movie.getOriginalTitle());

        // VOTER AVERAGE / RATING
        ratingTV.setText (String.valueOf(movie.getVoterAverage ()) + " / 10");

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
        System.out.print("HELLO!!!" + movie.getMovieId ());

        new ReviewsAsyncTask ().execute(String.valueOf(movie.getMovieId ()), Constants.REVIEW_URL_QUERY_PARAM);
    }

    /***
     * ASYNC TASK FOR THE "WATCH TRAILER" BUTTON
     ***/
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

    /***
     * METHOD FOR YT VIDEO INTENT.
     * https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     ***/
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
            mReviewAdapter = new ReviewAdapter(movies);
            mRecyclerView.setAdapter(mReviewAdapter);
        }
    }

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
}