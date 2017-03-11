package com.example.didier.stage1.adapter;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions to handle Film JSON data.
 */
public final class FilmJsonUtils {
    public static final String MDB_poster_path_String = "poster_path";
    public static final String MDB_adult_boolean = "adult";
    public static final String MDB_overview_String = "overview";
    public static final String MDB_release_date_String = "release_date";
    public static final String MDB_genre_ids_intArray = "genre_ids";
    public static final String MDB_id_int = "id";
    public static final String MDB_original_title_String = "original_title";
    public static final String MDB_original_language_String = "original_language";
    public static final String MDB_title_String = "title";
    public static final String MDB_backdrop_path_String = "backdrop_path";
    public static final String MDB_popularity_Number = "popularity";
    public static final String MDB_vote_count_Integer = "vote_count";
    public static final String MDB_video_Boolean = "video";
    public static final String MDB_vote_average_Number = "vote_average";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the Films.
     *
     * @param filmJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getMoviesFromJson(String filmJsonStr)
            throws JSONException {


        final String MDB_STATUS_MSG = "status_message";
        final String MDB_STATUS_CODE = "status_code";

        final String MDB_RESULTS = "results";

        JSONObject mdbJson = new JSONObject(filmJsonStr);

        /* Is there an error? */
        if (mdbJson.has(MDB_STATUS_MSG)) {
            int statusCode = mdbJson.getInt(MDB_STATUS_CODE);
            String statusMessage = mdbJson.getString(MDB_STATUS_MSG);

            Log.e("FILM_NETWORK", "Error (" + statusCode + ") - " + statusMessage);
            return null;
        }

        JSONArray movies = mdbJson.getJSONArray(MDB_RESULTS);

        ContentValues[] parsedMovieDB = new ContentValues[movies.length()];

        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            ContentValues valueMovie = new ContentValues();
            parsedMovieDB[i] = valueMovie;
            retrieveString(movie, valueMovie, MDB_id_int);
            retrieveString(movie, valueMovie, MDB_poster_path_String);
            retrieveString(movie, valueMovie, MDB_original_title_String);
            retrieveString(movie, valueMovie, MDB_overview_String);
            retrieveNumber(movie, valueMovie, MDB_vote_average_Number);
            retrieveInteger(movie, valueMovie, MDB_vote_count_Integer);
            retrieveString(movie, valueMovie, MDB_release_date_String);

        }

        return parsedMovieDB;
    }

    private static void retrieveInteger(JSONObject movie, ContentValues valueMovie, String fieldName) {
        int value = 0;
        if (movie.has(fieldName))
            try {
                value = movie.getInt(fieldName);
            } catch (JSONException e) {
                Log.e("FILM_NETWORK", "Error parsing " + fieldName + " - " + e.getLocalizedMessage());
            }
        valueMovie.put(fieldName, value);
    }

    private static void retrieveNumber(JSONObject movie, ContentValues valueMovie, String fieldName) {
        double value = 0.0;
        if (movie.has(fieldName))
            try {
                value = movie.getDouble(fieldName);
            } catch (JSONException e) {
                Log.e("FILM_NETWORK", "Error parsing " + fieldName + " - " + e.getLocalizedMessage());
            }
        valueMovie.put(fieldName, value);
    }

    private static void retrieveString(JSONObject movie, ContentValues valueMovie, String fieldName) throws JSONException {
        String value = "Not available";
        if (movie.has(fieldName))
            value = movie.getString(fieldName);
        valueMovie.put(fieldName, value);

    }
}