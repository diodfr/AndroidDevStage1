package com.example.didier.stage1.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.didier.stage1.R;
import com.example.didier.stage1.movies.MovieDbContract;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
    private static final String TAG = ReviewsAdapter.class.getName();
    private Cursor cursor;

    public ReviewsAdapter() {
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for this view holder
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View thisItemsView = myInflater.inflate(R.layout.review_list_item_layout, parent, false);
        return new ReviewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setReview(cursor);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void setCursor(Cursor cursor) {
        Cursor previousCursor = this.cursor;
        this.cursor = cursor;

        if (cursor == null || previousCursor == null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(previousCursor.getCount(), getItemCount() - previousCursor.getCount());
        }

        if (previousCursor != null)
            previousCursor.close();
    }

    public void reset() {
        this.cursor = null;
        notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        private final TextView author;
        private final TextView content;
        private final TextView url;

        ReviewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.reviewAuthor);
            content = (TextView) itemView.findViewById(R.id.reviewContent);
            url = (TextView) itemView.findViewById(R.id.reviewUrl);
        }

        void setReview(Cursor cursor) {
            String authorTxt = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieReview.COLUMN_AUTHOR));
            String contentTxt = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieReview.COLUMN_CONTENT));
            String urlTxt = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieReview.COLUMN_URL));

            author.setText(authorTxt);
            content.setText(contentTxt);
            url.setText(urlTxt);
        }
    }
}
