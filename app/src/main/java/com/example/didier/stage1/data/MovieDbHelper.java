package com.example.didier.stage1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.MatrixCursor;
import android.util.Log;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.adapter.FilmJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

class MovieDbHelper {
    private static final String TAG = MovieDbHelper.class.getName();

    private final Context context;
    private String[] columnsNames;

    public MovieDbHelper(Context context) {
        this.context = context;
    }

    public static final ContentValues[] loadFilms(String apiKey, boolean sortByPopular, int pageNumber) {

        URL api = NetworkUtils.getAPIURL(apiKey, sortByPopular, pageNumber);
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(api);
            Log.d(TAG, response);
            return FilmJsonUtils.getMoviesFromJson(response);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    private static class CursorData extends MatrixCursor {
        public static final String[] columnsNames = {
                MovieDbContract.FavoriteEntry.COLUMN_NAME,
                MovieDbContract.FavoriteEntry.COLUMN_POSTER_PATH,
                MovieDbContract.FavoriteEntry.COLUMN_ADULT,
                MovieDbContract.FavoriteEntry.COLUMN_OVERVIEW,
                MovieDbContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                MovieDbContract.FavoriteEntry.COLUMN_TITLE,
                MovieDbContract.FavoriteEntry.COLUMN_ORIGINAL_LANGUAGE,
                MovieDbContract.FavoriteEntry.COLUMN_POPULARITY,
                MovieDbContract.FavoriteEntry.COLUMN_VOTE_COUNT,
                MovieDbContract.FavoriteEntry.COLUMN_VIDEO,
                MovieDbContract.FavoriteEntry.COLUMNM_VOTE_AVERAGE,
                MovieDbContract.FavoriteEntry.COLUMNM_FAVORITE
        };

        private int pageCount = 0;

        public CursorData(Context context) {
            super(columnsNames);
        }

        @Override
        public boolean onMove(int oldPosition, int newPosition) {
            return false;
        }

        private void loadNewPage() {
            context.
        }
    }
}