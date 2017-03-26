package com.example.didier.stage1.movies;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.didier.stage1.R;

public class MovieDbContract {

    public static final String CONTENT_AUTHORITY = "com.example.didier.stage1.movies";
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE_DETAILS = "moviesDetails";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public enum SORT_TYPE {
        POPULARITY(R.id.order_popularity, R.string.order_popularity, MovieDbContract.MovieDetailEntry.COLUMN_POPULARITY),
        MOST_RATED(R.id.order_rated, R.string.order_rated, MovieDbContract.MovieDetailEntry.COLUMNM_VOTE_AVERAGE),
        FAVORITE(R.id.order_favorite, R.string.order_favorite, MovieDbContract.MovieDetailEntry.COLUMNM_FAVORITE);

        public final int menuId;
        public final int menuName;
        public final String columnName;

        SORT_TYPE(int menuId, int menuName, String columnName) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.columnName = columnName;
        }

        public static SORT_TYPE byMenuId(int itemId) {
            for (SORT_TYPE type : SORT_TYPE.values()) {
                if (type.menuId == itemId) return type;
            }

            return POPULARITY;
        }
    }

    public static final class MovieEntry1 implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMNM_FAVORITE = "favorite";
        public static final String COLUMN_ERROR_KEY = "ERROR_COLUMN";
        static final String TABLE_NAME = "favorites";

        public static Uri buildUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }


    }

    public static final class MovieDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_DETAILS)
                .build();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMNM_VOTE_AVERAGE = "vote_average";
        public static final String COLUMNM_FAVORITE = "favorite";
        public static final String COLUMN_ERROR_KEY = "ERROR_COLUMN";

        public static Uri buildUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }


    }

    public static final class MovieVideo implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEOS)
                .build();

        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_DESC = "description";

        public static Uri buildUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }

    public static final class MovieReview implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();

        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }

}
