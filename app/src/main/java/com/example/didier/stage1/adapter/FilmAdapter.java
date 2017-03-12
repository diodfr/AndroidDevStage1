package com.example.didier.stage1.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.R;
import com.example.didier.stage1.data.MovieDbContract;
import com.squareup.picasso.Picasso;

public class FilmAdapter extends CursorAdapter implements View.OnClickListener {

    private final FilmAdapterOnClickHandler mClickHandler;

    public FilmAdapter(Context context, Cursor cursor, FilmAdapterOnClickHandler clickHandler) {
        super(context, cursor, false);
        this.mClickHandler = clickHandler;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View view = myInflater.inflate(R.layout.movie_list_item_layout, parent, false);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int movieId = cursor.getInt(cursor.getColumnIndex(MovieDbContract.FavoriteEntry._ID));
        String imageId = cursor.getString(cursor.getColumnIndex(MovieDbContract.FavoriteEntry.COLUMN_POSTER_PATH));
        ImageView imageView = (ImageView) view.findViewById(R.id.movie_poster);

        Picasso.with(imageView.getContext())
                .load(NetworkUtils.getImageURL(imageId, imageView.getWidth()))
                .into(imageView);

        imageView.setTag(movieId);
    }

    @Override
    public void onClick(View view) {
        int movieId = (int) view.getTag();
        mClickHandler.onClick(movieId);
    }

    public interface FilmAdapterOnClickHandler {
        void onClick(int film);
    }
}
