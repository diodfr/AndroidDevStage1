/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.didier.stage1.movies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieDbProvider extends ContentProvider {

    public static final String TAG = MovieDbProvider.class.getName();

    public static final int CODE_MOVIEDB = 100;
    public static final int CODE_MOVIEDB_WITH_ID = 101;
    public static final int CODE_MOVIEDB_DETAIL_WITH_ID = 102;
    public static final int CODE_MOVIEDB_WITH_ID_VIDEOS = 200;
    public static final int CODE_MOVIEDB_WITH_ID_REVIEWS = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private List<List<Object>> films = new ArrayList<>();
    private int currentPage = 0;
    private String currentSort;

    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieDbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieDbContract.PATH_MOVIES, CODE_MOVIEDB);

        matcher.addURI(authority, MovieDbContract.PATH_MOVIES + "/#", CODE_MOVIEDB_WITH_ID);
        matcher.addURI(authority, MovieDbContract.PATH_MOVIE_DETAILS + "/#", CODE_MOVIEDB_DETAIL_WITH_ID);
        matcher.addURI(authority, MovieDbContract.PATH_VIDEOS + "/#", CODE_MOVIEDB_WITH_ID_VIDEOS);
        matcher.addURI(authority, MovieDbContract.PATH_REVIEWS + "/#", CODE_MOVIEDB_WITH_ID_REVIEWS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return 0;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIEDB_WITH_ID: {
                String idString = uri.getLastPathSegment();
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieDbContract.MovieEntry1.TABLE_NAME,
                        projection,
                        MovieDbContract.MovieEntry1._ID + " = ?",
                        new String[]{idString},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_MOVIEDB_DETAIL_WITH_ID: {
                cursor = getDetails(Integer.valueOf(uri.getLastPathSegment()));

                break;
            }


            case CODE_MOVIEDB: {
                if (MovieDbContract.SORT_TYPE.FAVORITE.columnName.equals(sortOrder)) {
                    cursor = mOpenHelper.getReadableDatabase().query(
                            MovieDbContract.MovieEntry1.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null);
                } else {
                    if (this.currentSort == null || this.currentSort.equals(sortOrder)) {
                        this.currentSort = sortOrder;
                        currentPage = 0;
                    }

                    updateFilms();
                    cursor = MovieVolatileDbHelper.createCursor(films);
                }
                break;
            }

            case CODE_MOVIEDB_WITH_ID_VIDEOS: {
                cursor = getVideos(Integer.valueOf(uri.getLastPathSegment()));
                break;
            }

            case CODE_MOVIEDB_WITH_ID_REVIEWS: {
                cursor = getReviews(Integer.valueOf(uri.getLastPathSegment()));
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null && cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getVideos(int filmId) {
        if (getContext() != null && NetworkUtils.isOnline(getContext())) {
            String apiKey = getContext().getString(R.string.API_KEY);
            ContentValues[] contentValues = MovieVolatileDbHelper.loadVideos(apiKey, filmId);
            return MovieVolatileDbHelper.createVideosCursor(contentValues);
        }

        return null;
    }

    private Cursor getReviews(int filmId) {
        if (getContext() != null && NetworkUtils.isOnline(getContext())) {
            String apiKey = getContext().getString(R.string.API_KEY);
            ContentValues[] contentValues = MovieVolatileDbHelper.loadReviews(apiKey, filmId);
            return MovieVolatileDbHelper.createReviewsCursor(contentValues);
        }

        return null;
    }

    private void updateFilms() {
        if (getContext() != null && NetworkUtils.isOnline(getContext())) {
            String apiKey = getContext().getString(R.string.API_KEY);

            ContentValues[] contentValues = MovieVolatileDbHelper.loadFilms(apiKey, MovieDbContract.SORT_TYPE.POPULARITY.columnName.equals(currentSort), currentPage + 1);
            Set<Integer> favorites = getFavorites();
            MovieVolatileDbHelper.updateFavorites(films, favorites);
            List<List<Object>> convertFilms = MovieVolatileDbHelper.convertFilms(contentValues, favorites);
            films.addAll(convertFilms);
            currentPage++;
            Log.d(TAG, "--->" + films.size());
        }
    }

    private Cursor getDetails(int filmId) {
        if (getContext() != null && NetworkUtils.isOnline(getContext())) {
            String apiKey = getContext().getString(R.string.API_KEY);
            ContentValues[] contentValues = MovieVolatileDbHelper.loadDetails(apiKey, filmId);
            boolean favorite = isFavorite(filmId);

            return MovieVolatileDbHelper.createDetailCursor(contentValues, favorite);
        }

        return null;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIEDB_WITH_ID:
                String idString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{idString};

                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieDbContract.MovieEntry1.TABLE_NAME,
                        MovieDbContract.MovieEntry1._ID + " = ? ",
                        selectionArguments);
                Log.d(TAG, "DELETE FAVORITE " + idString + " /:\\ " + numRowsDeleted);

        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Movie db.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIEDB_WITH_ID: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                long id = db.insert(MovieDbContract.MovieEntry1.TABLE_NAME, null, values);
                Log.d(TAG, "ADD FAVORITE " + id + " " + values.getAsString(MovieDbContract.MovieEntry1.COLUMN_TITLE));

                return MovieDbContract.MovieEntry1.buildUriWithId(id);
            }

            default:
                throw new RuntimeException("We are not implementing getType in Movie db.");
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }

    public Set<Integer> getFavorites() {
        Set<Integer> favorites = new HashSet<>();
        Cursor favoritesCursor = query(MovieDbContract.MovieEntry1.CONTENT_URI, null, null, null, MovieDbContract.SORT_TYPE.FAVORITE.columnName);
        if (favoritesCursor == null) {
            Log.e(TAG, "empty cursor");
            return favorites;
        }

        try {

            int idColumn = favoritesCursor.getColumnIndex(MovieDbContract.MovieEntry1._ID);
            if (idColumn < 0) {
                Log.e(TAG, "id column is not available");
                return favorites;
            }

            while (favoritesCursor.moveToNext()) {
                favorites.add(favoritesCursor.getInt(idColumn));
            }

            return favorites;
        } finally {
            favoritesCursor.close();
        }
    }

    public boolean isFavorite(int filmId) {
        Cursor favoritesCursor = query(MovieDbContract.MovieEntry1.CONTENT_URI, null, MovieDbContract.MovieEntry1._ID + " = ?", new String[]{String.valueOf(filmId)}, MovieDbContract.SORT_TYPE.FAVORITE.columnName);
        if (favoritesCursor == null) return false;
        try {
            return favoritesCursor.moveToFirst();
        } finally {
            favoritesCursor.close();
        }
    }
}