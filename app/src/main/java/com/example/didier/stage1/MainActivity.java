package com.example.didier.stage1;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
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
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FilmAdapter.FilmAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ContentValues> {

    //TODO FAVORITE
    //TODO LOADER
    public static final int NB_OF_COLUMN = 3;
    private static final int FILM_LOADER = 100;
    private ProgressBar mLoadingIndicator;
    private TextView mError;
    private FilmAdapter filmAdapter;
    private boolean sortPopularity = true;
    private int currentPage = 0;
    private boolean loading = false;

    public static int calculateNoOfColumns(Context context, int nbOfColumn) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        return (int) (dpWidth / nbOfColumn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        mError = (TextView) findViewById(R.id.errorIndicator);

        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, NB_OF_COLUMN, GridLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(layoutManager);


        int optimumImageSize = calculateNoOfColumns(this, NB_OF_COLUMN);
        filmAdapter = new FilmAdapter(this, optimumImageSize);
        myRecyclerView.setAdapter(filmAdapter);


        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount * 0.7) {
                            loadFilms();
                        }
                    }
                }
            }
        });

        loadFilms();

        getLoaderManager().initLoader(FILM_LOADER, null, this);
    }

    private void loadFilms() {
        currentPage++;
        new LoadFilmTask().execute(sortPopularity, currentPage);
    }

    @Override
    public void onClick(ContentValues film) {
        Intent intent = new Intent(this, FilmDetailActivity.class);
        intent.putExtra(FilmDetailActivity.FILM, film);

        startActivity(intent);
    }

    @Override
    public Loader<ContentValues> onCreateLoader(int id, Bundle args) {
        return;
    }

    @Override
    public void onLoadFinished(Loader<ContentValues> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<ContentValues> loader) {

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
            currentPage = 0;
            filmAdapter.reset();
            String newTitle = getString(sortPopularity ? R.string.order_popularity : R.string.order_rated);
            item.setTitle(newTitle);
            loadFilms();
        }

        return true;
    }

    class LoadFilmTask extends AsyncTask<Object, Void, ContentValues[]> {
        private static final String ERROR_KEY = "ERROR";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mError.setVisibility(View.INVISIBLE);
            loading = true;
        }

        @Override
        protected ContentValues[] doInBackground(Object... params) {
            URL api = NetworkUtils.getAPIURL(getString(R.string.API_KEY), (Boolean) params[0], (Integer) params[1]);
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
                filmAdapter.setFilms(Arrays.asList(results));
            }
            loading = false;
        }

        private boolean isError(ContentValues[] results) {
            return results.length == 1 && results[0].containsKey(ERROR_KEY);
        }
    }
}
