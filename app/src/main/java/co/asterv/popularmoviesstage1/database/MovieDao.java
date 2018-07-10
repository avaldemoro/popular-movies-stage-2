package co.asterv.popularmoviesstage1.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import co.asterv.popularmoviesstage1.model.Movie;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    Movie[] loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
