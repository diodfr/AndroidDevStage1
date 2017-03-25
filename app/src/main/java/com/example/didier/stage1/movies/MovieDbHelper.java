package com.example.didier.stage1.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.adapter.FilmJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MovieDbHelper {
    private static final String TAG = MovieDbHelper.class.getName();
    private static final String[] COLUMNS_NAME = {
            MovieDbContract.MovieEntry._ID,
            MovieDbContract.MovieEntry.COLUMN_TITLE,
            MovieDbContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDbContract.MovieEntry.COLUMN_ADULT,
            MovieDbContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDbContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieDbContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieDbContract.MovieEntry.COLUMN_POPULARITY,
            MovieDbContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieDbContract.MovieEntry.COLUMN_VIDEO,
            MovieDbContract.MovieEntry.COLUMNM_VOTE_AVERAGE,
            MovieDbContract.MovieEntry.COLUMNM_FAVORITE
    };

    private static final Map<String, String> COLUMNS_MAPPING = createColumnMapping();
    private static final String[] VIDEOS_COLUMNS = {
            MovieDbContract.MovieVideo._ID,
            MovieDbContract.MovieVideo.COLUMN_DESC,
            MovieDbContract.MovieVideo.COLUMN_KEY,
            MovieDbContract.MovieVideo.COLUMN_SITE,
    };
    private static final String[] REVIEWS_COLUMNS = {
            MovieDbContract.MovieReview._ID,
            MovieDbContract.MovieReview.COLUMN_AUTHOR,
            MovieDbContract.MovieReview.COLUMN_CONTENT,
            MovieDbContract.MovieReview.COLUMN_URL,
    };

    private static Map<String, String> createColumnMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(MovieDbContract.MovieEntry._ID, FilmJsonUtils.MDB_id_int);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_TITLE, FilmJsonUtils.MDB_original_title_String);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_POSTER_PATH, FilmJsonUtils.MDB_poster_path_String);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_ADULT, FilmJsonUtils.MDB_adult_boolean);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_OVERVIEW, FilmJsonUtils.MDB_overview_String);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_RELEASE_DATE, FilmJsonUtils.MDB_release_date_String);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, FilmJsonUtils.MDB_original_language_String);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_POPULARITY, FilmJsonUtils.MDB_popularity_Number);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_VOTE_COUNT, FilmJsonUtils.MDB_vote_count_Integer);
        mapping.put(MovieDbContract.MovieEntry.COLUMN_VIDEO, FilmJsonUtils.MDB_video_Boolean);
        mapping.put(MovieDbContract.MovieEntry.COLUMNM_VOTE_AVERAGE, FilmJsonUtils.MDB_vote_average_Number);

        return mapping;
    }

    public static final ContentValues[] loadFilms(String apiKey, boolean sortByPopular, int pageNumber) {
        URL url = NetworkUtils.getAPIURL(apiKey, sortByPopular, pageNumber);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            return FilmJsonUtils.getMoviesFromJson(response);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static final ContentValues[] loadDetails(String apiKey, int filmId) {
        URL url = NetworkUtils.getDetailAPIURL(apiKey, filmId);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            return FilmJsonUtils.getMovieFromJson(response);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static final ContentValues[] loadVideos(String apiKey, int filmId) {
        URL url = NetworkUtils.getVideosAPIURL(apiKey, filmId);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            return FilmJsonUtils.getVideosFromJson(response);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static final ContentValues[] loadReviews(String apiKey, int filmId) {
        URL url = NetworkUtils.getReviewsAPIURL(apiKey, filmId);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Log.d(TAG, response);
            return FilmJsonUtils.getReviewsFromJson(response);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static final Cursor createCursor(List<List<Object>> films) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS_NAME, films.size());

        for (List<Object> film : films)
            cursor.addRow(film);

        return cursor;
    }

    public static final List<List<Object>> convertFilms(ContentValues[] films, Set<Integer> favorite) {
        List<List<Object>> filmsDescription = new ArrayList<>();
        for (ContentValues film : films) {
            List<Object> filmDescription = new ArrayList<>();
            for (String column : COLUMNS_NAME) {
                if (column.equals(MovieDbContract.MovieEntry.COLUMNM_FAVORITE))
                    filmDescription.add(favorite.contains(film.getAsInteger(FilmJsonUtils.MDB_id_int)) ? 1 : 0);
                else if (COLUMNS_MAPPING.containsKey(column)) {
                    filmDescription.add(film.get(COLUMNS_MAPPING.get(column)));
                }
            }

            filmsDescription.add(filmDescription);
        }

        return filmsDescription;
    }

    public final static void updateFavorites(List<List<Object>> films, Set<Integer> favorites) {
        int columnId = findColumn(COLUMNS_NAME, MovieDbContract.MovieEntry._ID);
        int columnFavorite = findColumn(COLUMNS_NAME, MovieDbContract.MovieEntry.COLUMNM_FAVORITE);
        for (List<Object> film : films) {
            int id = (Integer) film.get(columnId);
            film.set(columnFavorite, favorites.contains(id) ? 1 : 0);
        }
    }

    private final static int findColumn(String[] columnsName, String column) {
        for (int i = 0; i < columnsName.length; i++) {
            if (column.equals(columnsName[i])) return i;
        }

        return -1;
    }

    public static final Cursor createDetailCursor(ContentValues[] filmDetail, boolean favorite) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS_NAME, 1);

        List<Object> values = new ArrayList<>();
        ContentValues film = filmDetail[0];

        for (String column : COLUMNS_NAME) {
            if (column.equals(MovieDbContract.MovieEntry.COLUMNM_FAVORITE))
                values.add(favorite ? 1 : 0);
            else if (COLUMNS_MAPPING.containsKey(column)) {
                values.add(film.get(COLUMNS_MAPPING.get(column)));
            }
        }

        cursor.addRow(values);
        return cursor;
    }

    public static final Cursor createVideosCursor(ContentValues[] videos) {
        MatrixCursor cursor = new MatrixCursor(VIDEOS_COLUMNS, videos.length);

        for (ContentValues video : videos) {
            List<Object> values = new ArrayList<>(VIDEOS_COLUMNS.length);

            for (String column : VIDEOS_COLUMNS) {
                switch (column) {
                    case MovieDbContract.MovieVideo._ID:
                        values.add(video.getAsString(FilmJsonUtils.MDB_VIDEO_ID_string));
                        break;
                    default:
                    case MovieDbContract.MovieVideo.COLUMN_DESC:
                        String type = video.getAsString(FilmJsonUtils.MDB_VIDEO_TYPE_string);
                        String name = video.getAsString(FilmJsonUtils.MDB_VIDEO_NAME_string);
                        values.add("[" + type + "]" + name);
                        break;
                    case MovieDbContract.MovieVideo.COLUMN_KEY:
                        String key = video.getAsString(FilmJsonUtils.MDB_VIDEO_KEY_string);
                        values.add(key);
                        break;
                    case MovieDbContract.MovieVideo.COLUMN_SITE:
                        String site = video.getAsString(FilmJsonUtils.MDB_VIDEO_SITE_string);
                        values.add(site);
                        break;

                }
            }

            cursor.addRow(values);
        }

        return cursor;
    }


    public static final Cursor createReviewsCursor(ContentValues[] reviews) {
        MatrixCursor cursor = new MatrixCursor(REVIEWS_COLUMNS, reviews.length);

        for (ContentValues review : reviews) {
            List<Object> values = new ArrayList<>(REVIEWS_COLUMNS.length);

            for (String column : REVIEWS_COLUMNS) {
                switch (column) {
                    case MovieDbContract.MovieReview._ID:
                        values.add(review.getAsString(FilmJsonUtils.MDB_REVIEW_ID_string));
                        break;
                    default:
                    case MovieDbContract.MovieReview.COLUMN_AUTHOR:
                        values.add(review.getAsString(FilmJsonUtils.MDB_REVIEW_AUTHOR_string));
                        break;
                    case MovieDbContract.MovieReview.COLUMN_CONTENT:
                        values.add(review.getAsString(FilmJsonUtils.MDB_REVIEW_CONTENT_string));
                        break;
                    case MovieDbContract.MovieReview.COLUMN_URL:
                        values.add(review.getAsString(FilmJsonUtils.MDB_REVIEW_URL_string));
                        break;
                }
            }

            cursor.addRow(values);
        }

        return cursor;
    }

}