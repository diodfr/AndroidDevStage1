package com.example.didier.stage1;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.didier.stage1.adapter.ReviewsAdapter;
import com.example.didier.stage1.adapter.VideosAdapter;
import com.example.didier.stage1.movies.MovieDbContract;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class FilmDetailActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String FILM = "FILM";
    public static final int LOADER_DETAIL = 200;
    public static final int LOADER_VIDEO = 201;
    public static final int LOADER_REVIEW = 202;
    private CheckBox favorite;
    private String filmName;
    private TextView mOriginalTitle;
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mSynopsis;
    private ImageView mThumbnail;
    private int filmId;
    private VideosAdapter videoAdapter;
    private ReviewsAdapter reviewsAdapter;
    private Set<AsyncQueryHandler> queryHandlers = new HashSet<>(); // Queries handlers to prevent garbage collection
    private String thumbnailPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        filmId = getIntent().getIntExtra(FILM, -1);

        mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
        mReleaseDate = (TextView) findViewById(R.id.releaseDate);
        mUserRating = (TextView) findViewById(R.id.userRating);
        mSynopsis = (TextView) findViewById(R.id.synopsis);
        mThumbnail = (ImageView) findViewById(R.id.thumbnailPoster);
        favorite = (CheckBox) findViewById(R.id.favorite);

        favorite.setOnClickListener(this);

        RecyclerView mVideos = (RecyclerView) findViewById(R.id.videos);
        mVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        videoAdapter = new VideosAdapter(this);
        mVideos.setAdapter(videoAdapter);

        RecyclerView mReviews = (RecyclerView) findViewById(R.id.reviews);
        mReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        reviewsAdapter = new ReviewsAdapter();
        mReviews.setAdapter(reviewsAdapter);

        getSupportLoaderManager().restartLoader(LOADER_DETAIL, null, this);
        getSupportLoaderManager().restartLoader(LOADER_VIDEO, null, this);
        getSupportLoaderManager().restartLoader(LOADER_REVIEW, null, this);
    }

    @Override
    public void onClick(View v) {
        if (favorite.equals(v)) {
            updateFavorite();
        }
    }

    private void updateFavorite() {
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                super.onDeleteComplete(token, cookie, result);
                queryHandlers.remove(this);
            }

            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                queryHandlers.remove(this);
            }
        };

        queryHandlers.add(queryHandler);

        Log.d(FilmDetailActivity.class.getName(), "updateFavorite => " + favorite.isChecked());

        Uri uriFavorite = MovieDbContract.MovieEntry1.buildUriWithId(filmId);

        if (!favorite.isChecked()) {
            queryHandler.startDelete(filmId, null, uriFavorite, null, null);
        } else {
            ContentValues values = new ContentValues();
            values.put(MovieDbContract.MovieEntry1._ID, filmId);

            values.put(MovieDbContract.MovieEntry1.COLUMN_TITLE, filmName);
            values.put(MovieDbContract.MovieEntry1.COLUMN_POSTER_PATH, thumbnailPath);
            values.put(MovieDbContract.MovieEntry1.COLUMN_TITLE, filmName);
            values.put(MovieDbContract.MovieEntry1.COLUMNM_FAVORITE, 1);

            queryHandler.startInsert(filmId, null, uriFavorite, values);
        }

        Log.d(FilmDetailActivity.class.getName(), "updateFavorite <= " + favorite.isChecked());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = null;
        switch (id) {
            case LOADER_DETAIL:
                uri = MovieDbContract.MovieDetailEntry.buildUriWithId(filmId);
                break;
            case LOADER_VIDEO:
                uri = MovieDbContract.MovieVideo.buildUriWithId(filmId);
                break;
            case LOADER_REVIEW:
                uri = MovieDbContract.MovieReview.buildUriWithId(filmId);
                break;

        }
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_DETAIL:
                updateDetails(data);
                break;
            case LOADER_VIDEO:
                videoAdapter.setCursor(data);
                break;
            case LOADER_REVIEW:
                reviewsAdapter.setCursor(data);
                break;
        }
    }

    private void updateDetails(Cursor values) {
        values.moveToFirst();
        filmName = values.getString(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMN_TITLE));
        mOriginalTitle.setText(filmName);

        mReleaseDate.setText(values.getString(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMN_RELEASE_DATE)));

        mUserRating.setText(values.getString(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMNM_VOTE_AVERAGE)));

        mSynopsis.setText(values.getString(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMN_OVERVIEW)));

        thumbnailPath = values.getString(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMN_POSTER_PATH));
        Picasso.with(this)
                .load(NetworkUtils.getImageURL(thumbnailPath))
                .into(mThumbnail);

        int favorite = values.getInt(values.getColumnIndex(MovieDbContract.MovieDetailEntry.COLUMNM_FAVORITE));
        this.favorite.setChecked(favorite > 0);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
