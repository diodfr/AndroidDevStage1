package com.example.didier.stage1.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.didier.stage1.R;
import com.example.didier.stage1.movies.MovieDbContract;


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoHolder> {
    private static final String TAG = VideosAdapter.class.getName();
    private Cursor cursor;
    private Activity activity;

    public VideosAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for this view holder
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View thisItemsView = myInflater.inflate(R.layout.video_list_item_layout, parent, false);
        return new VideoHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setFilm(cursor);
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

    class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Button videoIcon;
        private final TextView videoTitle;
        private String key;

        VideoHolder(View itemView) {
            super(itemView);

            videoIcon = (Button) itemView.findViewById(R.id.videoIcon);
            videoTitle = (TextView) itemView.findViewById(R.id.videoTitle);

            videoIcon.setOnClickListener(this);
            videoTitle.setOnClickListener(this);
        }

        void setFilm(Cursor cursor) {
            key = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieVideo.COLUMN_KEY));
            String desc = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieVideo.COLUMN_DESC));
            //String site = cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieVideo.COLUMN_SITE));

            videoTitle.setText(desc);
        }

        @Override
        public void onClick(View v) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));

            if (appIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                activity.startActivity(appIntent);
            } else {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                if (webIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    activity.startActivity(webIntent);
                } else {
                    Toast.makeText(v.getContext(), R.string.VIDEO_APPLICATION_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
