package com.example.didier.stage1;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.didier.stage1.adapter.FilmJsonUtils;
import com.squareup.picasso.Picasso;

public class FilmDetailActivity extends AppCompatActivity {
    public static final String FILM = "FILM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        ContentValues values = getIntent().getParcelableExtra(FILM);

        TextView mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
        mOriginalTitle.setText(values.getAsString(FilmJsonUtils.MDB_original_title_String));

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
    }
}
