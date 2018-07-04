package co.asterv.popularmoviesstage1;

import co.asterv.popularmoviesstage1.utils.Constants;
import co.asterv.popularmoviesstage1.utils.JsonUtils;
import co.asterv.popularmoviesstage1.model.Movie;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

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
        mRecyclerView.setLayoutManager(mLayoutManager);


        // SPINNER METHODS
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        final int currentSelection = sortSpinner.getSelectedItemPosition();

        if (isOnline () == true) {
            sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (currentSelection == i) {
                        // If most popular was selected
                        new FetchDataAsyncTask().execute(Constants.POPULAR_QUERY_PARAM);
                    } else {
                        // If top rated was selected
                        new FetchDataAsyncTask().execute(Constants.TOP_RATED_QUERY_PARAM);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), Constants.NO_INTERNET_TEXT, Constants.TOAST_DURATION).show();
        }

    }

    /***
     * METHOD TO MAKE STRING OF MOVIE DATA TO AN ARRAY OF MOVIE OBJECTS
     ***/

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
        }
        return movies;
    }

    /***
    * FETCH MOVIE DATA ASYNC TASK
    ***/

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
            } catch (IOException e) {
                return null;
            }

            try {
                return makeMoviesDataToArray (movieSearchResults);
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

    /***
     * CHECKS IF ONLINE
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     ***/

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
}
