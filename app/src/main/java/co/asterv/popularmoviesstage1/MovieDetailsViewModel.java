package co.asterv.popularmoviesstage1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import co.asterv.popularmoviesstage1.database.AppDatabase;
import co.asterv.popularmoviesstage1.model.Movie;

public class MovieDetailsViewModel extends ViewModel{

    private LiveData<Movie> movie;

    public MovieDetailsViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao ().loadMovieById (movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
