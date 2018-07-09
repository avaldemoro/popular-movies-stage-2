package co.asterv.popularmoviesstage1;

import co.asterv.popularmoviesstage1.database.AppDatabase;
import co.asterv.popularmoviesstage1.utils.Constants;
import co.asterv.popularmoviesstage1.utils.JsonUtils;
import co.asterv.popularmoviesstage1.model.Movie;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static android.widget.AdapterView.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppDatabase mDb;

    Movie[] movies;
    ImageAdapter mImageAdapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        mRecyclerView = findViewById (R.id.recycler_view);

        // Using a Grid Layout Manager
        mLayoutManager = new GridLayoutManager(this, Constants.GRID_NUM_OF_COLUMNS);
        mRecyclerView.getRecycledViewPool ().clear ();
        mRecyclerView.setLayoutManager(mLayoutManager);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar ();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

        // Check if online
        if (isOnline ()) {
            //Default to Popular Query Sort
            new FetchDataAsyncTask().execute(Constants.POPULAR_QUERY_PARAM);
        } else {
            Toast.makeText(getApplicationContext(), Constants.NO_INTERNET_TEXT, Constants.TOAST_DURATION).show();
        }
        mDb = AppDatabase.getInstance (getApplicationContext ());
    }

    @Override
    protected void onResume() {
        super.onResume ();

        //mImageAdapter.setMovies (mDb.movieDao ().loadAllMovies ());


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
    }

    /*** MENU METHOD ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate(R.menu.preference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
        if (id == R.id.popular_setting) {

            new FetchDataAsyncTask ().execute(Constants.POPULAR_QUERY_PARAM);
        } else if (id == R.id.top_rated_setting) {
            new FetchDataAsyncTask().execute(Constants.TOP_RATED_QUERY_PARAM);
        } else {
            // TODO: IF FAVORITES IS CLICKED
            AppExecutor.getInstance ().diskIO ().execute (new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        final Movie[] movies = mDb.movieDao ().loadAllMovies ();
                        @Override
                        public void run() {
                            mImageAdapter.notifyDataSetChanged ();
                            mImageAdapter.setMovies (movies);
                        }
                    });
                }

            });
        }

        return super.onOptionsItemSelected (item);
    }

    /*** METHOD TO MAKE STRING OF MOVIE DATA TO AN ARRAY OF MOVIE OBJECTS ***/
    public Movie[] makeMoviesDataToArray(String moviesJsonResults) throws JSONException {

        // Get results as an array
        JSONObject moviesJson = new JSONObject(moviesJsonResults);
        JSONArray resultsArray = moviesJson.getJSONArray(Constants.RESULTS_QUERY_PARAM);

        // Create array of Movie objects that stores data from the JSON string
        movies = new Movie[resultsArray.length()];

        // Go through movies one by one and get data
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setOriginalTitle(movieInfo.getString(Constants.ORIGINAL_TITLE_QUERY_PARAM));
            movies[i].setPosterPath(movieInfo.getString(Constants.POSTER_PATH_QUERY_PARAM));
            movies[i].setOverview(movieInfo.getString(Constants.OVERVIEW_QUERY_PARAM));
            movies[i].setVoterAverage(movieInfo.getDouble(Constants.VOTER_AVERAGE_QUERY_PARAM));
            movies[i].setReleaseDate(movieInfo.getString(Constants.RELEASE_DATE_QUERY_PARAM));
            movies[i].setMovieId (movieInfo.getInt (Constants.MOVIE_ID_QUERY_PARAM));
        }
        return movies;
    }

    /*** FETCH MOVIE DATA ASYNC TASK ***/
    public class FetchDataAsyncTask extends AsyncTask<String, Void, Movie[]> {
        public FetchDataAsyncTask() {
            super();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            // Holds data returned from the API
            String movieSearchResults = null;

            try {
                URL url = JsonUtils.buildUrl(params);
                movieSearchResults = JsonUtils.getResponseFromHttpUrl(url);

                if(movieSearchResults == null) {
                    return null;
                }
                return makeMoviesDataToArray (movieSearchResults);
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            return null;
        }

        protected void onPostExecute(Movie[] movies) {
            mImageAdapter = new ImageAdapter(getApplicationContext(), movies);
            mRecyclerView.setAdapter(mImageAdapter);
        }
    }

    /*** CHECKS IF ONLINE
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out ***/
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec(Constants.INTERNET_CHECK_COMMAND);
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
    /**
     * No Predictive Animations GridLayoutManager
     */

}