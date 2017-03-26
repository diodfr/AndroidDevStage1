package com.example.didier.stage1.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.R;
import com.example.didier.stage1.movies.MovieDbContract;
import com.squareup.picasso.Picasso;


public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmHolder> {
    private static final String TAG = FilmAdapter.class.getName();
    private final FilmAdapterOnClickHandler mClickHandler;
    private Cursor filmCursor;
    private int imageWidth;

    public FilmAdapter(FilmAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public FilmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for this view holder
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View thisItemsView = myInflater.inflate(R.layout.movie_list_item_layout, parent, false);
        return new FilmHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(FilmHolder holder, int position) {
        filmCursor.moveToPosition(position);
        holder.setFilm(filmCursor);
    }

    @Override
    public int getItemCount() {
        return filmCursor != null ? filmCursor.getCount() : 0;
    }

    public void setFilms(Cursor filmsCursor, int imageWidth) {
        Cursor previousCursor = this.filmCursor;
        this.filmCursor = filmsCursor;
        this.imageWidth = imageWidth;

        if (filmsCursor == null || previousCursor == null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(previousCursor.getCount(), getItemCount() - previousCursor.getCount());
        }

        if (previousCursor != null)
            previousCursor.close();
    }

    public void reset() {
        this.filmCursor = null;
        notifyDataSetChanged();
    }

    public interface FilmAdapterOnClickHandler {
        void onClick(int film);
    }

    class FilmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final CheckBox favoriteCheck;
        private int movieId;

        FilmHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.movie_poster);
            favoriteCheck = (CheckBox) itemView.findViewById(R.id.favoritePOSTER);
            itemView.setOnClickListener(this);
        }

        void setFilm(Cursor cursor) {
            movieId = cursor.getInt(cursor.getColumnIndex(MovieDbContract.MovieEntry1._ID));
            String imageId = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieEntry1.COLUMN_POSTER_PATH));
            boolean fav = cursor.getInt(cursor.getColumnIndex(MovieDbContract.MovieEntry1.COLUMNM_FAVORITE)) > 0;

            Log.d(TAG, "image width = " + imageWidth);


            Picasso picasso = Picasso.with(imageView.getContext());
            //picasso.setIndicatorsEnabled(true);
            //picasso.setLoggingEnabled(true);
            picasso.load(NetworkUtils.getImageURL(imageId, imageWidth))
                    .into(imageView);

            favoriteCheck.setChecked(fav);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(movieId);
        }
    }
}
