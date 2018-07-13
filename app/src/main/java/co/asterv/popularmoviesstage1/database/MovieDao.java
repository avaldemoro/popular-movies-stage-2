package co.asterv.popularmoviesstage1.database;

import android.arch.persistence.room.*;
import co.asterv.popularmoviesstage1.model.Movie;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    Movie[] loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateMovie(Movie movie);

    @Query("DELETE FROM movie WHERE dbMovieId = :movieId")
    void deleteMovie(int movieId);
}
