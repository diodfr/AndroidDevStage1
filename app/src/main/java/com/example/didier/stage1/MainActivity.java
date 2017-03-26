package com.example.didier.stage1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.example.didier.stage1.movies.MovieDbContract;



public class MainActivity extends AppCompatActivity implements FilmAdapter.FilmAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int NB_OF_COLUMN = 3;
    private static final int FILM_LOADER = 100;
    private static final String TAG = MainActivity.class.getName();
    private TextView mError;
    private ProgressBar mLoadingIndicator;
    private FilmAdapter filmAdapter;
    private boolean loading = false;
    private MovieDbContract.SORT_TYPE sort = MovieDbContract.SORT_TYPE.POPULARITY;

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

        filmAdapter = new FilmAdapter(this);
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
    }

    private void loadFilms() {
        if (NetworkUtils.isOnline(this)) {
            mError.setVisibility(View.INVISIBLE);
            LoaderManager.enableDebugLogging(true);
            loading = true;
            getSupportLoaderManager().restartLoader(FILM_LOADER, null, this);
        } else {
            mError.setText("Network Connection not available");
            mError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(int film) {
        Intent intent = new Intent(this, FilmDetailActivity.class);
        intent.putExtra(FilmDetailActivity.FILM, film);

        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "onCreateLoader");
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);

        return new CursorLoader(this, MovieDbContract.MovieEntry1.CONTENT_URI, null, null, null, sort.columnName);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.e(TAG, "onLoadFinished " + (data != null ? data.getCount() : "NULL"));
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (isError(data)) {
            mError.setText(getErrorMsg(data));
            mError.setVisibility(View.VISIBLE);
        } else {
            filmAdapter.setFilms(data, getImageWidth());
        }
        loading = false;
    }

    private String getErrorMsg(Cursor data) {
        if (data == null) return getString(R.string.UNKNON_ERROR);
        return data.getString(data.getColumnIndex(MovieDbContract.MovieEntry1.COLUMN_ERROR_KEY));
    }

    private boolean isError(Cursor data) {
        return data == null || data.getColumnIndex(MovieDbContract.MovieEntry1.COLUMN_ERROR_KEY) >= 0;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG, "onLoaderReset");
        loading = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getGroupId() == R.id.order) {
            filmAdapter.reset();

            sort = MovieDbContract.SORT_TYPE.byMenuId(item.getItemId());

            // FIXME String newTitle = item.getTitle().toString();

            loadFilms();
        }

        return true;
    }

    public int getImageWidth() {
        return calculateNoOfColumns(this, 3);
    }
}