package co.asterv.popularmoviesstage1.database;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import co.asterv.popularmoviesstage1.model.Movie;
import java.lang.Double;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

@SuppressWarnings("unchecked")
public class MovieDao_Impl implements MovieDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfMovie;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfMovie;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfMovie;

  public MovieDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMovie = new EntityInsertionAdapter<Movie>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `movie`(`dbMovieId`,`movieId`,`originalTitle`,`posterPath`,`overview`,`releaseDate`,`voterAverage`,`trailerPath`,`reviewAuthor`,`reviewContents`,`reviewUrl`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Movie value) {
        stmt.bindLong(1, value.getDbMovieId());
        stmt.bindLong(2, value.getMovieId());
        if (value.getOriginalTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getOriginalTitle());
        }
        if (value.getPosterPath() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getPosterPath());
        }
        if (value.getOverview() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getOverview());
        }
        if (value.getReleaseDate() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getReleaseDate());
        }
        if (value.getVoterAverage() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindDouble(7, value.getVoterAverage());
        }
        if (value.getTrailerPath() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTrailerPath());
        }
        if (value.getReviewAuthor() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getReviewAuthor());
        }
        if (value.getReviewContents() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getReviewContents());
        }
        if (value.getReviewUrl() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getReviewUrl());
        }
      }
    };
    this.__deletionAdapterOfMovie = new EntityDeletionOrUpdateAdapter<Movie>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `movie` WHERE `dbMovieId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Movie value) {
        stmt.bindLong(1, value.getDbMovieId());
      }
    };
    this.__updateAdapterOfMovie = new EntityDeletionOrUpdateAdapter<Movie>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR REPLACE `movie` SET `dbMovieId` = ?,`movieId` = ?,`originalTitle` = ?,`posterPath` = ?,`overview` = ?,`releaseDate` = ?,`voterAverage` = ?,`trailerPath` = ?,`reviewAuthor` = ?,`reviewContents` = ?,`reviewUrl` = ? WHERE `dbMovieId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Movie value) {
        stmt.bindLong(1, value.getDbMovieId());
        stmt.bindLong(2, value.getMovieId());
        if (value.getOriginalTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getOriginalTitle());
        }
        if (value.getPosterPath() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getPosterPath());
        }
        if (value.getOverview() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getOverview());
        }
        if (value.getReleaseDate() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getReleaseDate());
        }
        if (value.getVoterAverage() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindDouble(7, value.getVoterAverage());
        }
        if (value.getTrailerPath() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTrailerPath());
        }
        if (value.getReviewAuthor() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getReviewAuthor());
        }
        if (value.getReviewContents() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getReviewContents());
        }
        if (value.getReviewUrl() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getReviewUrl());
        }
        stmt.bindLong(12, value.getDbMovieId());
      }
    };
  }

  @Override
  public void insertMovie(Movie movie) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfMovie.insert(movie);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteMovie(Movie movie) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfMovie.handle(movie);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateMovie(Movie movie) {
    __db.beginTransaction();
    try {
      __updateAdapterOfMovie.handle(movie);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Movie[] loadAllMovies() {
    final String _sql = "SELECT * FROM movie";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDbMovieId = _cursor.getColumnIndexOrThrow("dbMovieId");
      final int _cursorIndexOfMovieId = _cursor.getColumnIndexOrThrow("movieId");
      final int _cursorIndexOfOriginalTitle = _cursor.getColumnIndexOrThrow("originalTitle");
      final int _cursorIndexOfPosterPath = _cursor.getColumnIndexOrThrow("posterPath");
      final int _cursorIndexOfOverview = _cursor.getColumnIndexOrThrow("overview");
      final int _cursorIndexOfReleaseDate = _cursor.getColumnIndexOrThrow("releaseDate");
      final int _cursorIndexOfVoterAverage = _cursor.getColumnIndexOrThrow("voterAverage");
      final int _cursorIndexOfTrailerPath = _cursor.getColumnIndexOrThrow("trailerPath");
      final int _cursorIndexOfReviewAuthor = _cursor.getColumnIndexOrThrow("reviewAuthor");
      final int _cursorIndexOfReviewContents = _cursor.getColumnIndexOrThrow("reviewContents");
      final int _cursorIndexOfReviewUrl = _cursor.getColumnIndexOrThrow("reviewUrl");
      final Movie[] _result = new Movie[_cursor.getCount()];
      int _index = 0;
      while(_cursor.moveToNext()) {
        final Movie _item;
        _item = new Movie();
        final int _tmpDbMovieId;
        _tmpDbMovieId = _cursor.getInt(_cursorIndexOfDbMovieId);
        _item.setDbMovieId(_tmpDbMovieId);
        final int _tmpMovieId;
        _tmpMovieId = _cursor.getInt(_cursorIndexOfMovieId);
        _item.setMovieId(_tmpMovieId);
        final String _tmpOriginalTitle;
        _tmpOriginalTitle = _cursor.getString(_cursorIndexOfOriginalTitle);
        _item.setOriginalTitle(_tmpOriginalTitle);
        final String _tmpPosterPath;
        _tmpPosterPath = _cursor.getString(_cursorIndexOfPosterPath);
        _item.setPosterPath(_tmpPosterPath);
        final String _tmpOverview;
        _tmpOverview = _cursor.getString(_cursorIndexOfOverview);
        _item.setOverview(_tmpOverview);
        final String _tmpReleaseDate;
        _tmpReleaseDate = _cursor.getString(_cursorIndexOfReleaseDate);
        _item.setReleaseDate(_tmpReleaseDate);
        final Double _tmpVoterAverage;
        if (_cursor.isNull(_cursorIndexOfVoterAverage)) {
          _tmpVoterAverage = null;
        } else {
          _tmpVoterAverage = _cursor.getDouble(_cursorIndexOfVoterAverage);
        }
        _item.setVoterAverage(_tmpVoterAverage);
        final String _tmpTrailerPath;
        _tmpTrailerPath = _cursor.getString(_cursorIndexOfTrailerPath);
        _item.setTrailerPath(_tmpTrailerPath);
        final String _tmpReviewAuthor;
        _tmpReviewAuthor = _cursor.getString(_cursorIndexOfReviewAuthor);
        _item.setReviewAuthor(_tmpReviewAuthor);
        final String _tmpReviewContents;
        _tmpReviewContents = _cursor.getString(_cursorIndexOfReviewContents);
        _item.setReviewContents(_tmpReviewContents);
        final String _tmpReviewUrl;
        _tmpReviewUrl = _cursor.getString(_cursorIndexOfReviewUrl);
        _item.setReviewUrl(_tmpReviewUrl);
        _result[_index] = _item;
        _index ++;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
