package co.asterv.popularmoviesstage1.utils;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class JsonUtils {
    public static URL buildUrl(String[] query) throws MalformedURLException {
        Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon()
                .appendPath(query[0])
                .appendQueryParameter(Constants.API_KEY_QUERY_PARAM, Constants.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieIdUrl(String id, String query) throws MalformedURLException {
        Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(query)
                .appendQueryParameter(Constants.API_KEY_QUERY_PARAM, Constants.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}