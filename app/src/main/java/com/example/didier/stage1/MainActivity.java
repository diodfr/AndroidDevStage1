package com.example.didier.stage1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.didier.stage1.adapter.FilmAdapter;
import com.example.didier.stage1.adapter.FilmJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements FilmAdapter.FilmAdapterOnClickHandler {

    public static final int NB_OF_COLUMN = 3;
    private ProgressBar mLoadingIndicator;
    private TextView mError;
    private FilmAdapter filmAdapter;
    private boolean sortPopularity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        mError = (TextView) findViewById(R.id.errorIndicator);

        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, NB_OF_COLUMN, GridLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(layoutManager);


        int optimumImageSize = calculateNoOfColumns(this, NB_OF_COLUMN);
        filmAdapter = new FilmAdapter(this, optimumImageSize);
        myRecyclerView.setAdapter(filmAdapter);

        loadFilms();
    }

    public static int calculateNoOfColumns(Context context, int nbOfColumn) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        return (int) (dpWidth / nbOfColumn);
    }

    private void loadFilms() {
        new LoadFilmTask().execute(sortPopularity);
    }

    @Override
    public void onClick(ContentValues film) {
        Intent intent = new Intent(this, FilmDetailActivity.class);
        intent.putExtra(FilmDetailActivity.FILM, film);

        startActivity(intent);
    }

    class LoadFilmTask extends AsyncTask<Boolean, Void, ContentValues[]> {
        private static final String ERROR_KEY = "ERROR";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mError.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ContentValues[] doInBackground(Boolean... sort) {
            URL api = NetworkUtils.getAPIURL(getString(R.string.API_KEY), sort[0]);
            try {
                if (!isOnline()) {
                    return createError("No internet connection available.");
                }
                String response = NetworkUtils.getResponseFromHttpUrl(api);
                Log.d(NetworkUtils.FILM_NETWORK, response);
                return FilmJsonUtils.getMoviesFromJson(MainActivity.this, response);

            } catch (IOException e) {
                Log.e(NetworkUtils.FILM_NETWORK, e.getLocalizedMessage());
                return createError("Error while retrieving data from movieDB.");
            } catch (JSONException e) {
                Log.e(NetworkUtils.FILM_NETWORK, e.getLocalizedMessage());
                return createError("Incorrect data retrieved from movieDB.");
            }
        }

        @NonNull
        private ContentValues[] createError(String message) {
            ContentValues error = new ContentValues();
            error.put(ERROR_KEY, message);
            return new ContentValues[]{
                    error
            };
        }

        @Override
        protected void onPostExecute(ContentValues[] results) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (isError(results)) {
                mError.setText(results[0].getAsString(ERROR_KEY));
                mError.setVisibility(View.VISIBLE);
            } else {
                filmAdapter.setFilms(results);
            }
        }

        private boolean isError(ContentValues[] results) {
            return results.length == 1 && results[0].containsKey(ERROR_KEY);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.order) {
            sortPopularity = !sortPopularity;
            String newTitle = getString(sortPopularity ? R.string.order_popularity : R.string.order_rated);
            item.setTitle(newTitle);
            loadFilms();
        }

        return true;
    }
}
