package com.example.didier.stage1;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.didier.stage1.adapter.FilmJsonUtils;
import com.example.didier.stage1.data.FavoriteFilmsContract;
import com.squareup.picasso.Picasso;

import static com.example.didier.stage1.adapter.FilmJsonUtils.MDB_id_int;

public class FilmDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String FILM = "FILM";
    private CheckBox favortite;
    private int id;
    private Uri uriFavorite;
    private String filmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        ContentValues values = getIntent().getParcelableExtra(FILM);

        TextView mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
        filmName = values.getAsString(FilmJsonUtils.MDB_original_title_String);
        mOriginalTitle.setText(filmName);

        TextView mReleaseDate = (TextView) findViewById(R.id.releaseDate);
        mReleaseDate.setText(values.getAsString(FilmJsonUtils.MDB_release_date_String));

        TextView mUserRating = (TextView) findViewById(R.id.userRating);
        mUserRating.setText(values.getAsString(FilmJsonUtils.MDB_vote_average_Number));

        TextView mSynopsis = (TextView) findViewById(R.id.synopsis);
        mSynopsis.setText(values.getAsString(FilmJsonUtils.MDB_overview_String));

        ImageView mThumbnail = (ImageView) findViewById(R.id.thumbnailPoster);
        String thumbnailID = values.getAsString(FilmJsonUtils.MDB_poster_path_String);
        Picasso.with(this)
                .load(NetworkUtils.getImageURL(thumbnailID))
                .into(mThumbnail);

        id = values.getAsInteger(MDB_id_int);
        uriFavorite = FavoriteFilmsContract.FavoriteEntry.buildFavoriteUriWithId(id);
        Cursor query = getContentResolver().query(uriFavorite, null, null, null, null);

        favortite = (CheckBox) findViewById(R.id.favorite);
        favortite.setChecked(query.getCount() > 0);
        favortite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (favortite.equals(v)) {
            updateFavorite();
        }
    }

    private void updateFavorite() {
        Log.d(FilmDetailActivity.class.getName(), "updateFavorite => " + favortite.isChecked());
        if (favortite.isChecked()) {
            getContentResolver().delete(uriFavorite, null, null);
        } else {
            ContentValues values = new ContentValues();
            values.put(FavoriteFilmsContract.FavoriteEntry._ID, id);

            values.put(FavoriteFilmsContract.FavoriteEntry.COLUMN_NAME, filmName);

            getContentResolver().insert(uriFavorite, values);
        }

        Log.d(FilmDetailActivity.class.getName(), "updateFavorite <= " + favortite.isChecked());

    }
}
