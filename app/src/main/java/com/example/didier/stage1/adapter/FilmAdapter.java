package com.example.didier.stage1.adapter;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.didier.stage1.NetworkUtils;
import com.example.didier.stage1.R;
import com.squareup.picasso.Picasso;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmHolder> {

    private final int optimumImageSize;
    private ContentValues[] films;

    private final FilmAdapterOnClickHandler mClickHandler;

    public interface FilmAdapterOnClickHandler {
        void onClick(ContentValues film);
    }

    public FilmAdapter(FilmAdapterOnClickHandler clickHandler, int optimumImageSize) {
        this.mClickHandler = clickHandler;
        this.optimumImageSize = optimumImageSize;
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
        holder.setFilm(films[position]);
    }

    @Override
    public int getItemCount() {
        return films != null ? films.length : 0;
    }

    public void setFilms(ContentValues[] films) {
        this.films = films;
        notifyDataSetChanged();
    }

    class FilmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private ContentValues film;

        FilmHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        void setFilm(ContentValues film) {
            this.film = film;

            String imageId = film.getAsString(FilmJsonUtils.MDB_poster_path_String);
            Picasso.with(imageView.getContext())
                    .load(NetworkUtils.getImageURL(imageId, optimumImageSize))
                    .into(imageView);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(film);
        }
    }
}
